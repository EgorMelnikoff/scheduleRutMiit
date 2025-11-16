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
    onCheckedChange: (Boolean) -> Unit,
    colors: SwitchColors? = null
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = colors ?: SwitchDefaults.colors().copy(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }
}