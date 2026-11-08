package com.egormelnikoff.schedulerutmiit.core.ui.components.check_box

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
fun AppCheckBox(
    checked: Boolean,
    colors: CheckboxColors? = null,
    onCheckedChange: (Boolean) -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = colors ?: CheckboxDefaults.colors().copy(
                checkedCheckmarkColor = AppTheme.colors.onPrimary,
                checkedBorderColor = AppTheme.colors.primary,
                checkedBoxColor = AppTheme.colors.primary,
                uncheckedBoxColor = Color.Transparent,
                uncheckedBorderColor = AppTheme.colors.onSecondaryContainer,
                uncheckedCheckmarkColor = AppTheme.colors.onSecondaryContainer,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppCheckBoxCheckedPreview() {
    PreviewLightTheme {
        AppCheckBox(
            checked = true
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun AppCheckBoxUncheckedPreview() {
    PreviewLightTheme {
        AppCheckBox(
            checked = false
        ) {}
    }
}