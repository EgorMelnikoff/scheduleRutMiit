package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CustomFilterChip(
    title: String,
    imageVector: ImageVector? = null,
    selected: Boolean = false,
    border: BorderStroke? = null,
    colors: SelectableChipColors? = null,
    onClick: ((Boolean) -> Unit)? = null
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
            onClick = onClick?.let {
                { onClick(!selected) }
            } ?: {},
            leadingIcon = imageVector?.let {
                {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = imageVector,
                        contentDescription = null
                    )
                }
            },
            label = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
            },
            enabled = onClick != null,
            selected = selected
        )
    }
}


@Composable
fun CustomAssistChip(
    title: String,
    imageVector: ImageVector? = null,
    border: BorderStroke? = null,
    colors: ChipColors? = null,
    enabled: Boolean = true,
    isUnspecifiedIconColor: Boolean = false,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        AssistChip(
            border = border,
            colors = colors ?: AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                trailingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            onClick = onClick,
            leadingIcon = imageVector?.let {
                {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = if (isUnspecifiedIconColor) Color.Unspecified else LocalContentColor.current
                    )
                }
            },
            label = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
            },
            enabled = enabled
        )
    }
}