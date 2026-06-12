package com.mapbox.dash.showcase.app.relaxedmode

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.showcase.app.R

internal class RelaxedModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val defaultMargin = dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(defaultMargin)
                    .background(colorResource(com.mapbox.dash.theming.R.color.background_blue))
                    .padding(defaultMargin),
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(24.dp)
                        .clickable { finish() },
                    imageVector = Icons.Default.Close,
                    contentDescription = "close button",
                    colorFilter = ColorFilter.tint(Color.White),
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Image placeholder should be here",
                    color = Color.White,
                    fontSize = 36.sp,
                )
            }
        }
    }
}
