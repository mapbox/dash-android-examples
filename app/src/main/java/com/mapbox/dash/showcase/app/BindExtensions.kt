package com.mapbox.dash.showcase.app

import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.navigation.mapgpt.MapGptComposeConfig
import com.mapbox.navigation.mapgpt.ui.MapGptCarouselCardParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal fun LifecycleOwner.bindSwitch(
    switch: SwitchCompat,
    state: MutableStateFlow<Boolean>,
    dashNavigationFragmentFlow: Flow<DashNavigationFragment?>,
    onSelected: (DashNavigationFragment, Boolean) -> Unit,
) {
    repeatWhenStarted(lifecycleOwner = this) {
        combine(dashNavigationFragmentFlow.filterNotNull(), state) { fragment, isChecked ->
            switch.isChecked = isChecked
            onSelected(fragment, isChecked)
        }.collect()
    }
    switch.setOnCheckedChangeListener { _, isChecked ->
        state.value = isChecked
    }
}

internal fun LifecycleOwner.bindSwitch(
    switch: SwitchCompat,
    state: MutableStateFlow<Boolean>,
    onSelected: (value: Boolean) -> Unit = {},
) {
    state.observeWhenStarted(lifecycleOwner = this) { isChecked ->
        switch.isChecked = isChecked
        onSelected(isChecked)
    }
    switch.setOnCheckedChangeListener { _, isChecked ->
        state.value = isChecked
    }
}

internal fun LifecycleOwner.bindMapGptComposeConfigSwitch(
    switch: SwitchCompat,
    config: MutableStateFlow<MapGptComposeConfig>,
    getValue: MapGptComposeConfig.() -> Boolean,
    setValue: MapGptComposeConfig.Builder.(Boolean) -> Unit,
) {
    config.map { it.getValue() }.distinctUntilChanged()
        .observeWhenStarted(this) { value ->
            switch.isChecked = value
        }
    switch.setOnCheckedChangeListener { _, isChecked ->
        config.value = config.value.build { setValue(isChecked) }
    }
}

internal fun LifecycleOwner.bindMapGptCarouselCardParams(
    switch: SwitchCompat,
    config: MutableStateFlow<MapGptCarouselCardParams?>,
    setValue: (isChecked: Boolean) -> MapGptCarouselCardParams? = { null },
) {
    config.observeWhenStarted(this) { value ->
        switch.isChecked = value != null
    }
    switch.setOnCheckedChangeListener { _, isChecked ->
        config.value = setValue(isChecked)
    }
}
