package com.mapbox.dash.example

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.mapbox.navigation.mapgpt.core.CoroutineMiddleware
import com.mapbox.navigation.mapgpt.core.audiofocus.AudioFocusOwner
import com.mapbox.navigation.mapgpt.core.language.Language
import com.mapbox.navigation.mapgpt.core.textplayer.Announcement
import com.mapbox.navigation.mapgpt.core.textplayer.PlayerCallback
import com.mapbox.navigation.mapgpt.core.textplayer.Voice
import com.mapbox.navigation.mapgpt.core.textplayer.VoicePlayer
import com.mapbox.navigation.mapgpt.core.textplayer.VoiceProgress
import com.mapbox.navigation.mapgpt.core.textplayer.middleware.VoicePlayerMiddleware
import com.mapbox.navigation.mapgpt.core.textplayer.middleware.VoicePlayerMiddlewareContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale

class LocalVoicePlayerMiddleware :
    VoicePlayerMiddleware,
    CoroutineMiddleware<VoicePlayerMiddlewareContext>() {

    private val _availableLanguages = MutableStateFlow(emptySet<Language>())
    override val availableLanguages: StateFlow<Set<Language>> = _availableLanguages.asStateFlow()

    private val _availableVoices = MutableStateFlow(emptySet<Voice>())
    override val availableVoices: StateFlow<Set<Voice>> = _availableVoices.asStateFlow()

    private var voicePlayer: LocalVoicePlayer? = null

    override fun onAttached(middlewareContext: VoicePlayerMiddlewareContext) {
        super.onAttached(middlewareContext)
        Log.i(TAG, "onAttached" )
        voicePlayer = LocalVoicePlayer(
            middlewareContext.platformContext.applicationContext,
        ).also { voicePlayer ->
            attachVoiceCapabilities(voicePlayer)
        }
    }

    override fun onDetached(middlewareContext: VoicePlayerMiddlewareContext) {
        super.onDetached(middlewareContext)
        Log.i(TAG, "onDetached")
        _availableLanguages.value = emptySet()
        _availableVoices.value = emptySet()
        release()
    }

    override fun prefetch(announcement: Announcement) {
        // No-op: nothing to prefetch
    }

    override fun play(
        announcement: Announcement,
        progress: VoiceProgress?,
        callback: PlayerCallback,
    ) {
        Log.d(TAG, "play $announcement")
        val middlewareContext = middlewareContext ?: run {
            callback.onError(announcement.utteranceId, "Middleware context is not available")
            return
        }

        middlewareContext.audioFocusManager.request(AudioFocusOwner.TextToSpeech) { isGranted ->
            if (isGranted) {
                voicePlayer?.play(announcement, progress, callback)
            } else {
                callback.onError(announcement.utteranceId, "Audio focus is not granted")
            }
        }
    }

    override fun stop() {
        voicePlayer?.stop()
    }

    override fun release() {
        voicePlayer?.release()
    }

    override fun volume(level: Float) {
        if (level in 0.0f..1.0f) {
            voicePlayer?.volume(level)
        }
    }

    private fun attachVoiceCapabilities(voicePlayer: VoicePlayer) {
        voicePlayer.availableLanguages.onEach { availableLanguages ->
            _availableLanguages.value = availableLanguages
        }.launchIn(ioScope)
        voicePlayer.availableVoices.onEach { availableVoices ->
            _availableVoices.value = availableVoices
        }.launchIn(ioScope)
    }

    private companion object {
        private const val TAG = "LocalVoicePlayerMiddleware"
    }
}

class AndroidTextToSpeechVoice(
    val voice: android.speech.tts.Voice,
) : Voice {
    override fun toString(): String {
        return "AndroidTextToSpeechVoice(voice=$voice)"
    }
}

internal class LocalVoicePlayer(
    androidContext: Context,
    private val initialLanguage: Language = Language(Locale.ENGLISH),
    private val initialTtsEngine: String = "com.google.android.tts",
) : VoicePlayer {

    private var textToSpeechInitStatus: Int? = null
    private var volume = 1.0f

    private val _availableLanguages = MutableStateFlow(emptySet<Language>())
    override val availableLanguages: StateFlow<Set<Language>> = _availableLanguages.asStateFlow()

    private val _availableVoices = MutableStateFlow(emptySet<Voice>())
    override val availableVoices: StateFlow<Set<Voice>> = _availableVoices.asStateFlow()

    private var language: Language = initialLanguage

    private val textToSpeech = TextToSpeech(
        androidContext,
        { status ->
            textToSpeechInitStatus = status
            if (status == TextToSpeech.SUCCESS) {
                initializeWithLanguage()
            }
        },
        initialTtsEngine,
    )

    private fun initializeWithLanguage() {
        Log.d(TAG, "Available tts engines: ${textToSpeech.engines.joinToString()}")
        val enginePackages = textToSpeech.engines.map { it.name }.toSet()
        if (!enginePackages.contains(initialTtsEngine)) {
            Log.w(TAG, "The initialTtsEngine was not found: $initialTtsEngine. " +
                        "Using ${textToSpeech.defaultEngine}")
        } else {
            Log.i(TAG, "Using tts engine: $initialTtsEngine")
        }
        try {
            // there is a chance to get NPE from ITextToSpeechService getting the voices that
            // means we're not able to use TTS
            // https://issuetracker.google.com/issues/37012397?pli=1
            textToSpeech.voices
        } catch (e: NullPointerException) {
            Log.w(TAG, "Calling textToSpeech.voices causes NullPointerException")
            textToSpeechInitStatus = TextToSpeech.ERROR
            return
        }

        val availableLocales = textToSpeech.availableLanguages
        Log.i(TAG, "Available languages: ${availableLocales.joinToString { it.isO3Language }}")
        _availableLanguages.value = availableLocales.map { Language(it) }.toSet()
        Log.d(TAG, "Searching for TTS language: ${initialLanguage.locale.isO3Language}")
        val targetLocale = availableLocales.firstOrNull {
            it.isO3Language == initialLanguage.locale.isO3Language
        }
        Log.i(TAG, "Found TTS language: ${targetLocale?.isO3Language}")
        val locale: Locale = targetLocale?.let {
            targetLocale.also { language = Language(it) }
        } ?: run {
            Log.w(TAG, "${language.locale} is not supported, using $defaultLocale")
            defaultLocale
        }

        updateAvailableVoices(locale)

        Log.d(TAG, "Set language to $locale")
        val setLanguageResult = textToSpeech.setLanguage(locale)
        Log.d(TAG, "setLanguageResult: $setLanguageResult")
        Log.d(TAG, "TextToSpeech voice: ${textToSpeech.voice}")
    }

    private fun updateAvailableVoices(locale: Locale) {
        val availableVoices = textToSpeech.voices.mapNotNull { voice ->
            val voiceLanguage = Language(voice.locale)
            if (voiceLanguage == language) {
                AndroidTextToSpeechVoice(voice)
            } else {
                null
            }
        }.toSet()
        if (availableVoices.isNotEmpty()) {
            _availableVoices.value = availableVoices
        } else {
            val defaultVoice = textToSpeech.voice
            Log.w(TAG, "No voice for $locale, fallback to $defaultVoice")
            _availableVoices.value = defaultVoice
                ?.let { setOf(AndroidTextToSpeechVoice(it)) }
                ?: emptySet()
        }
        Log.d(TAG, "Available voices: ${_availableVoices.value}")
    }

    override fun prefetch(announcement: Announcement) = Unit

    override fun play(
        announcement: Announcement,
        progress: VoiceProgress?,
        callback: PlayerCallback,
    ) {
        val startPosition = (progress as? VoiceProgress.Index)?.position ?: 0
        textToSpeech.setOnUtteranceProgressListener(
            UtteranceProgressListenerWrapper(
                announcement.text,
                startPosition,
                callback,
            ),
        )
        val bundle = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
        }
        if (textToSpeechInitStatus == TextToSpeech.SUCCESS) {
            textToSpeech.speak(
                announcement.text,
                TextToSpeech.QUEUE_FLUSH,
                bundle,
                announcement.utteranceId,
            )
        } else {
            callback.onError(announcement.utteranceId, "TTS is not initialized")
        }
    }

    override fun stop() {
        textToSpeech.stop()
    }

    override fun release() = Unit

    override fun volume(level: Float) {
        volume = level
    }

    inner class UtteranceProgressListenerWrapper(
        private val text: String?,
        private val initialStartPosition: Int,
        private val clientCallback: PlayerCallback,
    ) : UtteranceProgressListener() {

        private var currentPosition: Int = 0
        override fun onStart(utteranceId: String) {
            Log.d(TAG, "onStart $utteranceId")
            clientCallback.onStartPlaying(text = text, utteranceId = utteranceId)
        }

        override fun onRangeStart(utteranceId: String, start: Int, end: Int, frame: Int) {
            Log.d(TAG, "onRangeStart $utteranceId, start: $start, end $end, frame $frame")
            currentPosition = initialStartPosition + start
        }

        override fun onDone(utteranceId: String) {
            Log.d(TAG, "onDone $utteranceId")
            clientCallback.onComplete(text = text, utteranceId = utteranceId)
            currentPosition = 0
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String) {
            // Deprecated, may be called due to https://issuetracker.google.com/issues/138321382
            Log.d(TAG, "onError $utteranceId")
            clientCallback.onError(utteranceId, null)
            currentPosition = 0
        }

        override fun onError(utteranceId: String, errorCode: Int) {
            Log.d(TAG, "onError $utteranceId, errorCode: $errorCode")
            clientCallback.onError(utteranceId, errorCode.toString())
            currentPosition = 0
        }

        override fun onStop(utteranceId: String, interrupted: Boolean) {
            Log.d(TAG, "onStop $utteranceId, interrupted: $interrupted")
            clientCallback.onStop(
                utteranceId,
                VoiceProgress.Index(position = currentPosition),
            )
            currentPosition = 0
        }
    }

    private companion object {
        private const val TAG = "LocalVoicePlayer"
        private val defaultLocale = Locale.ENGLISH
    }
}
