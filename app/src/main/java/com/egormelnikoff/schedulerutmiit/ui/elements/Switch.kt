package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch (
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
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,


                disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(0.7f),
                disabledCheckedThumbColor = MaterialTheme.colorScheme.onPrimary.copy(0.9f),
                disabledUncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.4f),
                disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.4f),
                disabledUncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.7f)
            )
        )
    }
}