package com.egormelnikoff.schedulerutmiit.core.ui.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
fun AppFilterChip(
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
                containerColor = AppTheme.colors.secondaryContainer,
                labelColor = AppTheme.colors.onSecondaryContainer,
                iconColor = AppTheme.colors.onSecondaryContainer,

                selectedContainerColor = AppTheme.colors.primary,
                selectedLabelColor = AppTheme.colors.onPrimary,
                selectedLeadingIconColor = AppTheme.colors.onPrimary,
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
                    style = AppTheme.typography.titleSmall
                )
            },
            enabled = onClick != null,
            selected = selected
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppFilterChipPreview() {
    PreviewLightTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding6)
        ) {
            AppFilterChip(
                title = "Тест",
                selected = true
            ) {}
            AppFilterChip(
                title = "Тест",
                selected = false
            ) {}
        }

    }
}