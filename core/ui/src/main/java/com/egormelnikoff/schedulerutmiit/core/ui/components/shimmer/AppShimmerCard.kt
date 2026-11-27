package com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.primaryContainer,
    shimmerWidth: Float = 1000f,
    content: @Composable (Brush) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -shimmerWidth,
        targetValue = shimmerWidth * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            color.copy(alpha = 0.6f),
            color.copy(alpha = 0.3f),
            color.copy(alpha = 0.6f),
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + shimmerWidth, shimmerWidth)
    )

    Box(modifier = modifier) {
        content(brush)
    }
}

@Composable
fun AppShimmerCard(
    brush: Brush,
    modifier: Modifier = Modifier,
    height: Dp? = null,
    width: Dp? = null,
    shape: CornerBasedShape? = null
) {
    Box(
        modifier = modifier
            .let { if (width != null) it.width(width) else it.fillMaxWidth() }
            .let { if (height != null) it.height(height) else it }
            .clip(shape ?: AppTheme.shapes.medium)
            .background(brush)
    )
}