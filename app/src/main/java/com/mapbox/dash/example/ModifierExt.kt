package com.mapbox.dash.example

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("MagicNumber")
fun Modifier.shadow(
    blur: Dp = 8.dp,
    shape: Shape = RectangleShape,
): Modifier {
    val shadowColor = Color.Black.copy(alpha = 0.12f).toArgb()
    val drawModifier = drawBehind {
        val paint = Paint()
        paint.color = Color.Transparent
        paint.asFrameworkPaint().setShadowLayer(blur.toPx(), 0.dp.toPx(), 4.dp.toPx(), shadowColor)
        val path = Path()
        path.addOutline(shape.createOutline(size, layoutDirection, density = this))
        clipPath(path, ClipOp.Difference) {
            drawIntoCanvas { it.drawPath(path, paint) }
        }
    }
    return drawModifier.clip(shape)
}