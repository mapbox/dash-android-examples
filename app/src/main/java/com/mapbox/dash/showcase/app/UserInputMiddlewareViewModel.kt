package com.mapbox.dash.showcase.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.mapgpt.core.userinput.UserInputOwnerMiddleware
import kotlinx.coroutines.flow.MutableStateFlow

class UserInputMiddlewareViewModel(application: Application) : AndroidViewModel(application) {
    val selectedModelName = MutableStateFlow(MIDDLEWARE_DEFAULT)

    fun availableModels(): List<String> = listOf(
        MIDDLEWARE_DEFAULT,
    )

    /**
     * Returns a recognized middleware based on the selected model, or null otherwise. Null
     * represents the default middleware in this example.
     */
    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    @Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
    fun getUserInputMiddleware(model: String?): UserInputOwnerMiddleware? {
        return null
    }

    private companion object {
        private const val MIDDLEWARE_DEFAULT = "Default"
    }
}
