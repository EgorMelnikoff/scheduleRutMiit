package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CustomChip(
    title: String,
    imageVector: ImageVector?,
    selected: Boolean,
    onSelect: (Boolean) -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        FilterChip(
            border = null,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,

                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                iconColor = MaterialTheme.colorScheme.onPrimary,

            ),
            onClick = { onSelect(!selected) },
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (imageVector != null) {
                        Icon(
                            modifier = Modifier.width(16.dp),
                            imageVector = imageVector,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            selected = selected,
        )
    }
}

