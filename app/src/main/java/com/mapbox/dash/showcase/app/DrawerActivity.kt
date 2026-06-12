package com.mapbox.dash.showcase.app

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mapbox.dash.compose.shadow
import com.mapbox.dash.showcase.app.databinding.ActivityDrawerBinding

abstract class DrawerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDrawerBinding.inflate(layoutInflater) }

    private val drawerOpened = mutableStateOf(false)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.drawer.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        binding.drawer.setContent {
            Box(contentAlignment = Alignment.CenterStart) {
                Image(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
                        .shadow(4.dp, shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                        .size(25.dp, 60.dp)
                        .alpha(BUTTON_ALPHA)
                        .background(colorResource(com.mapbox.navigation.tripdata.R.color.colorSurface))
                        .clickable { drawerOpened.value = true },
                    contentScale = ContentScale.Inside,
                    painter = painterResource(R.drawable.ic_baseline_menu_24),
                    contentDescription = null,
                )
                AnimatedVisibility(
                    drawerOpened.value,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable(interactionSource = null, indication = null) { closeDrawers() },
                    )
                }
                val openedOffset = with(LocalDensity.current) {
                    when (val layoutDirection = LocalLayoutDirection.current) {
                        LayoutDirection.Ltr -> WindowInsets.safeDrawing.getLeft(this, layoutDirection).toDp()
                        LayoutDirection.Rtl -> WindowInsets.safeDrawing.getRight(this, layoutDirection).toDp()
                    }
                }
                val closedOffset = (-MENU_WIDTH).dp
                MenuView(
                    modifier = Modifier
                        .offset(x = animateDpAsState(if (drawerOpened.value) openedOffset else closedOffset).value)
                        .fillMaxHeight()
                        .width(MENU_WIDTH.dp)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState()),
                )
                if (drawerOpened.value) {
                    BackHandler { closeDrawers() }
                }
            }
        }
        setContentView(binding.root)
    }

    @Composable
    abstract fun MenuView(modifier: Modifier)

    fun closeDrawers() {
        drawerOpened.value = false
    }

    private companion object {
        private const val BUTTON_ALPHA = 0.7f
        private const val MENU_WIDTH = 300
    }
}
