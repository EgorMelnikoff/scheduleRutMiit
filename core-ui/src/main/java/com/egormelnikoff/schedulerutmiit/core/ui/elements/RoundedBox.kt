package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun RoundedBox(
    title: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    shape: CornerBasedShape? = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
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