package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CustomFilterChip(
    title: String,
    imageVector: ImageVector? = null,
    selected: Boolean = false,
    border: BorderStroke? = null,
    colors: SelectableChipColors? = null,
    onSelect:((Boolean) -> Unit)? = null
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        FilterChip(
            border = border,
            colors = colors ?: FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer,

                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
            ),
            onClick = onSelect?.let {
                { onSelect(!selected) }
            } ?: {},
            leadingIcon = if (imageVector != null) {
                {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = imageVector,
                        contentDescription = null
                    )
                }
            } else null,
            label = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
            },
            enabled = onSelect != null,
            selected = selected
        )
    }
}