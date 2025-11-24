package com.mapbox.dash.example

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.presentation.DefaultRightSidebarComposer
import com.mapbox.dash.driver.presentation.SidebarComposer
import com.mapbox.dash.driver.presentation.SidebarScope
import com.mapbox.dash.example.relaxedmode.RelaxedModeActivity
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.weather.model.WeatherSystemOfMeasurement
import com.mapbox.navigation.weather.model.toIconResId
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class DashRightSidebarComposer(
    private val visible: StateFlow<Boolean>,
    private val override: StateFlow<Boolean>,
    private val weatherVM: WeatherViewModel,
) : SidebarComposer by DefaultRightSidebarComposer {

    @Composable
    override fun SidebarScope.Content() {
        if (!visible.collectAsState().value) return
        if (override.collectAsState().value) {
            val context = LocalContext.current
            WeatherAlertWidget()
            Button(
                iconId = R.drawable.baseline_remove_red_eye_24,
                onClick = {
                    context.startActivity(Intent(context, RelaxedModeActivity::class.java))
                },
            )
            Speed()
            Spacer(modifier = Modifier.weight(1f))
            Button(
                iconId = R.drawable.ic_waving_hand,
                onClick = { Toast.makeText(context, "Hey, Dash!", Toast.LENGTH_SHORT).show() },
            )
            Routes()
            Debug()
            CurrentWeatherWidget()
        } else {
            with(DefaultRightSidebarComposer) {
                Content()
            }
        }
    }

    @Composable
    private fun WeatherAlertWidget(modifier: Modifier = Modifier) {
        val alerts = weatherVM.weatherAlertsAtMapCenter.collectAsState(null)
            .value?.joinToString(separator = "\n") { it.title }
            ?.takeUnless { it.isBlank() } ?: "No weather alerts in the center of the map"

        Text(
            modifier = modifier
                .shadow(4.dp, shape = CircleShape)
                .border(
                    width = 2.dp,
                    color = androidx.compose.ui.graphics.Color.Black,
                    shape = CircleShape,
                )
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(all = 20.dp),
            text = alerts,
            color = androidx.compose.ui.graphics.Color.Black,
        )
    }

    @Composable
    private fun CurrentWeatherWidget(modifier: Modifier = Modifier) {
        val conditions = weatherVM.weatherConditionAtMapCenter.collectAsState(null).value ?: return

        val unit = when (conditions.fields.systemOfMeasurement) {
            WeatherSystemOfMeasurement.Imperial -> "F"
            WeatherSystemOfMeasurement.Metric -> "C"
            else -> "C"
        }

        Box(
            modifier = modifier
                .width(90.dp)
                .height(90.dp)
                .shadow(8.dp, shape = CircleShape)
                .background(androidx.compose.ui.graphics.Color.White),
        ) {
            Image(
                modifier = Modifier
                    .align(BiasAlignment(horizontalBias = 0.4f, verticalBias = 0.4f))
                    .size(40.dp),
                painter = painterResource(conditions.fields.iconCode?.toIconResId() ?: -1),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.align(
                    BiasAlignment(horizontalBias = -0.3f, verticalBias = -0.3f),
                ),
                text = "${conditions.fields.temperature?.toInt()} °$unit",
                maxLines = 1,
                color = androidx.compose.ui.graphics.Color.Black,
            )
        }
    }
}