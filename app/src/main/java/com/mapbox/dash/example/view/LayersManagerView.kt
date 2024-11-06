package com.mapbox.dash.example.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.mapbox.dash.example.databinding.LayoutLayersManagerBinding
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.base.flow.observeWhenStarted
import com.mapbox.dash.sdk.base.layer.DashMapStyleLayer
import com.mapbox.dash.sdk.base.layer.DashMapStyleLayersConfig
import com.mapbox.dash.sdk.config.dsl.mapStyle
import kotlinx.coroutines.flow.MutableStateFlow

private const val TRANSIT_LAYER_ID = "transit-label"
private const val POI_LAYER_ID = "poi-label"

class LayersManagerCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CardView(context, attrs, defStyleAttr) {

    private fun getTransitLayer(visible: Boolean) = DashMapStyleLayer(id = TRANSIT_LAYER_ID, visible = visible)
    private fun getPoiLayer(visible: Boolean) = DashMapStyleLayer(id = POI_LAYER_ID, visible = visible)

    private val layers = MutableStateFlow(
        mapOf(
            TRANSIT_LAYER_ID to getTransitLayer(visible = true),
            POI_LAYER_ID to getPoiLayer(visible = true),
        ),
    )

    private val binding: LayoutLayersManagerBinding = LayoutLayersManagerBinding.inflate(
        LayoutInflater.from(context),
        this,
    )

    init {
        binding.header.setOnClickListener {
            binding.llToggles.isVisible = !binding.llToggles.isVisible
        }
        binding.toggleTransit.isChecked = true
        binding.toggleTransit.setOnCheckedChangeListener { _, isChecked ->
            layers.value = layers.value.toMutableMap().apply {
                this[TRANSIT_LAYER_ID] = getTransitLayer(visible = isChecked)
            }
        }
        binding.togglePoi.isChecked = true
        binding.togglePoi.setOnCheckedChangeListener { _, isChecked ->
            layers.value = layers.value.toMutableMap().apply {
                this[POI_LAYER_ID] = getPoiLayer(visible = isChecked)
            }
        }
        binding.toggleDistractingElements.setOnCheckedChangeListener { _, isChecked ->
            layers.value = if (isChecked) {
                emptyMap()
            } else {
                mapOf(
                    TRANSIT_LAYER_ID to getTransitLayer(visible = binding.toggleTransit.isChecked),
                    POI_LAYER_ID to getPoiLayer(visible = binding.togglePoi.isChecked),
                )
            }
            if (isChecked) {
                binding.toggleTransit.isEnabled = false
                binding.togglePoi.isEnabled = false
            } else {
                binding.toggleTransit.isEnabled = true
                binding.togglePoi.isEnabled = true
            }
        }
    }

    internal fun bind(lifecycleOwner: LifecycleOwner) {
        layers.observeWhenStarted(lifecycleOwner) {
            Dash.applyUpdate {
                mapStyle {
                    mapStyleLayersConfig = DashMapStyleLayersConfig(layers = it.values.toList())
                }
            }
        }
    }
}
