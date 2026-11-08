package com.egormelnikoff.schedulerutmiit.core.ui.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
fun AppAssistChip(
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
                containerColor = AppTheme.colors.secondaryContainer,
                labelColor = AppTheme.colors.onSecondaryContainer,
                leadingIconContentColor = AppTheme.colors.onSecondaryContainer,
                trailingIconContentColor = AppTheme.colors.onSecondaryContainer
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
                    style = AppTheme.typography.titleSmall
                )
            },
            enabled = enabled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppAssistChipPreview() {
    PreviewLightTheme {
        AppAssistChip(
            title = "Тест"
        ) {}
    }
}