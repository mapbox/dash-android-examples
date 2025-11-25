package com.mapbox.dash.example

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

private fun repeatWhen(
    state: Lifecycle.State,
    lifecycleOwner: LifecycleOwner,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    return lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state, block)
    }
}

fun <T> Flow<T>.observeWhenStarted(
    lifecycleOwner: LifecycleOwner,
    action: FlowCollector<T> = FlowCollector {},
): Job {
    return repeatWhen(Lifecycle.State.STARTED, lifecycleOwner) {
        collect(action)
    }
}
