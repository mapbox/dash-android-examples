package com.mapbox.dash.showcase.app

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.dash.sdk.config.api.MarkerOptions
import com.mapbox.dash.sdk.config.api.RoutePointMarkerFactory
import com.mapbox.dash.sdk.config.api.SearchCategory
import com.mapbox.dash.sdk.config.api.SearchSuggestionsMarkerFactory
import com.mapbox.dash.sdk.config.api.SymbolDescriptorRoutePoint
import com.mapbox.dash.sdk.config.api.SymbolDescriptorSearchSuggestions
import com.mapbox.dash.sdk.ev.domain.model.DashReachabilityType.NOT_REACHABLE

private const val SCALE_FACTOR = 1.5
private const val TEXT_OFFSET_EMS = 0.5

class SampleMarkerFactory(
    private val context: Context,
) : SearchSuggestionsMarkerFactory, RoutePointMarkerFactory {

    private val textOffset = listOf(0.0, TEXT_OFFSET_EMS)

    /**
     * Creates a marker for search suggestions symbol descriptors.
     */
    override fun create(
        activity: Activity,
        symbolDescriptor: SymbolDescriptorSearchSuggestions,
        selected: Boolean,
    ): MarkerOptions {
        Log.d("SampleMarkerFactory", "symbolDescriptor = $symbolDescriptor")
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
    override fun create(
        activity: Activity,
        symbolDescriptor: SymbolDescriptorRoutePoint,
        selected: Boolean,
    ): MarkerOptions {
        Log.d("SampleMarkerFactory", "symbolDescriptor = $symbolDescriptor")
        return MarkerOptions()
            .withImage(symbolDescriptor.image)
            .withImageAnchor(MarkerOptions.ImageAnchor.BOTTOM)
            .withImageScaleFactorSize(if (selected) SCALE_FACTOR else 1.0)
            .withTextAnchor(MarkerOptions.TextAnchor.BOTTOM)
    }

    private val SymbolDescriptorSearchSuggestions.haloColor: Int
        get() {
            val hex = when {
                categoryIds.contains(SearchCategory.Grocery.id) -> "#FF63A6E9"
                categoryIds.contains(SearchCategory.Coffee.id) -> "#FFFF9933"
                categoryIds.contains(SearchCategory.Food.id) -> "#FFFF9933"
                else -> "#FFF47BCB"
            }
            return Color.parseColor(hex)
        }

    private val SymbolDescriptorSearchSuggestions.image: MarkerOptions.Image.Bitmap
        get() {
            val resId = when {
                categoryIds.contains(SearchCategory.Grocery.id) -> if (evReachability == NOT_REACHABLE) {
                    R.drawable.ic_pin_grocery_not_reachable
                } else {
                    R.drawable.ic_pin_grocery
                }

                categoryIds.contains(SearchCategory.Coffee.id) -> if (evReachability == NOT_REACHABLE) {
                    R.drawable.ic_pin_coffee_not_reachable
                } else {
                    R.drawable.ic_pin_coffee
                }

                categoryIds.contains(SearchCategory.Food.id) -> if (evReachability == NOT_REACHABLE) {
                    R.drawable.ic_pin_food_not_reachable
                } else {
                    R.drawable.ic_pin_food
                }

                else -> if (evReachability == NOT_REACHABLE) {
                    R.drawable.ic_pin_default_not_reachable
                } else {
                    R.drawable.ic_pin_default
                }
            }
            return checkNotNull(ContextCompat.getDrawable(context, resId))
                .let { MarkerOptions.Image.Bitmap(it.toBitmap()) }
        }

    private val SymbolDescriptorRoutePoint.image: MarkerOptions.Image.Bitmap
        get() {
            val resId = when (type) {
                is SymbolDescriptorRoutePoint.Type.Destination -> {
                    if (evReachability == NOT_REACHABLE) {
                        R.drawable.ic_pin_destination_not_reachable
                    } else {
                        R.drawable.ic_pin_destination
                    }
                }

                else -> {
                    if (evReachability == NOT_REACHABLE) {
                        R.drawable.ic_pin_waypoint_not_reachable
                    } else {
                        R.drawable.ic_pin_waypoint
                    }
                }
            }

            return checkNotNull(ContextCompat.getDrawable(context, resId))
                .let { MarkerOptions.Image.Bitmap(it.toBitmap()) }
        }
}
