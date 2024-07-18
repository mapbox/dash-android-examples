package com.mapbox.dash.example

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.dash.sdk.config.api.MarkerFactory
import com.mapbox.dash.sdk.config.api.MarkerOptions
import com.mapbox.dash.sdk.config.api.SearchCategory
import com.mapbox.dash.sdk.config.api.SymbolDescriptorRoutePoint
import com.mapbox.dash.sdk.config.api.SymbolDescriptorSearchSuggestions

private const val SCALE_FACTOR = 1.5
private const val TEXT_OFFSET_EMS = 0.5

class SampleMarkerFactory(
    private val context: Context,
) : MarkerFactory {

    private val textOffset = listOf(0.0, TEXT_OFFSET_EMS)

    /**
     * Creates a marker for search suggestions symbol descriptors.
     */
    override fun create(symbolDescriptor: SymbolDescriptorSearchSuggestions, selected: Boolean): MarkerOptions {
        return MarkerOptions()
            .withImage(symbolDescriptor.image)
            .withImageAnchor(MarkerOptions.ImageAnchor.BOTTOM)
            .withImageScaleFactorSize(if (selected) SCALE_FACTOR else 1.0)
            .withTextField("#${symbolDescriptor.index + 1}")
            .withTextAnchor(MarkerOptions.TextAnchor.BOTTOM)
            .withTextColor(Color.WHITE)
            .withTextOffset(textOffset)
            .withTextHaloColor(symbolDescriptor.haloColor)
            .withTextHaloWidth(1.0)
    }

    /**
     * Creates a marker for route point symbol descriptors.
     */
    override fun create(symbolDescriptor: SymbolDescriptorRoutePoint, selected: Boolean): MarkerOptions {
        return MarkerOptions()
            .withImage(symbolDescriptor.image)
            .withImageAnchor(MarkerOptions.ImageAnchor.BOTTOM)
            .withImageScaleFactorSize(if (selected) SCALE_FACTOR else 1.0)
            .withTextAnchor(MarkerOptions.TextAnchor.BOTTOM)
    }

    private val SymbolDescriptorSearchSuggestions.haloColor: Int
        get() {
            val hex = when {
                categories.contains(SearchCategory.Grocery) -> "#FF63A6E9"
                categories.contains(SearchCategory.Coffee) -> "#FFFF9933"
                categories.contains(SearchCategory.Food) -> "#FFFF8126"
                else -> "#FFF47BCB"
            }
            return Color.parseColor(hex)
        }

    private val SymbolDescriptorSearchSuggestions.image: MarkerOptions.Image.Bitmap
        get() {
            val resId = when {
                categories.contains(SearchCategory.Grocery) -> R.drawable.ic_grocery
                categories.contains(SearchCategory.Coffee) -> R.drawable.ic_coffee
                categories.contains(SearchCategory.Food) -> R.drawable.ic_food
                else -> R.drawable.ic_fallback
            }
            return checkNotNull(ContextCompat.getDrawable(context, resId))
                .let { MarkerOptions.Image.Bitmap(it.toBitmap()) }
        }

    private val SymbolDescriptorRoutePoint.image: MarkerOptions.Image.Bitmap
        get() {
            return when (type) {
                is SymbolDescriptorRoutePoint.Type.Destination -> checkNotNull(
                    ContextCompat.getDrawable(context, R.drawable.ic_destination)
                )

                else -> checkNotNull(
                    ContextCompat.getDrawable(context, R.drawable.ic_destination)
                )
            }.let { MarkerOptions.Image.Bitmap(it.toBitmap()) }

        }
}
