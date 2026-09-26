package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                checkedCheckmarkColor = MaterialTheme.colorScheme.onPrimary,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                checkedBoxColor = MaterialTheme.colorScheme.primary,
                uncheckedBoxColor = Color.Transparent,
                uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedCheckmarkColor = MaterialTheme.colorScheme.onSecondaryContainer,
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