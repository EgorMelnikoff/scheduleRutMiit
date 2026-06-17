package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun RoundedBox(
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor)
    ) {
        content.invoke()
    }
}