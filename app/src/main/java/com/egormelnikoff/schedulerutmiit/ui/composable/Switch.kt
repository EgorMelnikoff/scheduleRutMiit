package com.egormelnikoff.schedulerutmiit.ui.composable

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp


@Composable
fun SwitchButton (
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colors: SwitchColors
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Switch(
            checked = checked,
            onCheckedChange = {
                onCheckedChange(it)
            },
            colors = colors
        )
    }
}