package com.egormelnikoff.schedulerutmiit.core.ui.components._switch

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun AppSwitch (
    checked: Boolean,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = colors ?: SwitchDefaults.colors().copy(
                checkedTrackColor = AppTheme.colors.primary,
                checkedThumbColor = AppTheme.colors.onPrimary,
                uncheckedBorderColor = AppTheme.colors.onSecondaryContainer,
                uncheckedThumbColor = AppTheme.colors.onSecondaryContainer,
                uncheckedTrackColor = AppTheme.colors.secondaryContainer,


                disabledCheckedTrackColor = AppTheme.colors.primary.copy(0.7f),
                disabledCheckedThumbColor = AppTheme.colors.onPrimary.copy(0.9f),
                disabledUncheckedBorderColor = AppTheme.colors.onSecondaryContainer.copy(0.4f),
                disabledUncheckedThumbColor = AppTheme.colors.onSecondaryContainer.copy(0.4f),
                disabledUncheckedTrackColor = AppTheme.colors.secondaryContainer.copy(0.7f)
            )
        )
    }
}