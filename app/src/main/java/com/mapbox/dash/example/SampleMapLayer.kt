package com.mapbox.dash.example

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.imageResource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.style.PointListValue
import com.mapbox.maps.extension.compose.style.layers.generated.RasterLayer
import com.mapbox.maps.extension.compose.style.sources.generated.rememberImageSourceState
import com.mapbox.maps.extension.style.sources.generated.ImageSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.sources.updateImage
import kotlinx.coroutines.delay

private const val ID_IMAGE_SOURCE = "image_source-id"

@Composable
fun SampleMapLayer() {
    val bitmaps = listOf(

        ImageBitmap.imageResource(R.drawable.southeast_radar_0).asAndroidBitmap(),
        ImageBitmap.imageResource(R.drawable.southeast_radar_1).asAndroidBitmap(),
        ImageBitmap.imageResource(R.drawable.southeast_radar_2).asAndroidBitmap(),
        ImageBitmap.imageResource(R.drawable.southeast_radar_3).asAndroidBitmap(),
    )
    MapEffect(Unit) {
        val imageSource: ImageSource = it.mapboxMap.getSourceAs(ID_IMAGE_SOURCE)!!
        var index = 0
        while (true) {
            imageSource.updateImage(bitmaps[index])
            delay(1000)
            index = (index + 1) % 4
        }
    }
    RasterLayer(
        sourceState = rememberImageSourceState(sourceId = ID_IMAGE_SOURCE) {
            coordinates = PointListValue(
                Point.fromLngLat(-81.98844, 43.150429),
                Point.fromLngLat(-73.07944, 43.150429),
                Point.fromLngLat(-73.07944, 34.649429),
                Point.fromLngLat(-81.98844, 34.649429)
            )
        }
    )
}


