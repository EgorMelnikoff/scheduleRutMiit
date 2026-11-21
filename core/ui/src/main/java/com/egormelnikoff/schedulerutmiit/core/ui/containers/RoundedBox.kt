package com.egormelnikoff.schedulerutmiit.core.ui.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun RoundedBox(
    title: String? = null,
    backgroundColor: Color = AppTheme.colors.secondaryContainer,
    shape: CornerBasedShape? = AppTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding4),
                text = it,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .let {
                    if (shape != null) it.clip(shape) else it
                }
                .background(backgroundColor)
        ) {
            content.invoke()
        }
    }

}