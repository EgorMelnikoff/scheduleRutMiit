package com.egormelnikoff.schedulerutmiit.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun LoadingCard(
    height: Dp? = null,
    width: Dp? = null,
    shape: CornerBasedShape? = null
) {
    Box(
        modifier = Modifier
            .let {
                if (width != null) {
                    it.width(width)
                } else {
                    it.fillMaxWidth()
                }
            }
            .let {
                if (height != null) {
                    it.height(height)
                } else it
            }
            .clip(shape ?: MaterialTheme.shapes.medium)
            .shimmer(
                color = MaterialTheme.colorScheme.primaryContainer
            )

    )
}

fun Modifier.shimmer(
    color: Color
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val shimmerWidth = 1000f

    val translateAnim = transition.animateFloat(
        initialValue = -shimmerWidth,
        targetValue = shimmerWidth * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            color.copy(alpha = 0.6f),
            color.copy(alpha = 0.3f),
            color.copy(alpha = 0.6f),
        ),
        start = Offset(translateAnim.value, 0f),
        end = Offset(translateAnim.value + shimmerWidth, shimmerWidth)
    )

    background(brush)
}