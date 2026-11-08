package com.egormelnikoff.schedulerutmiit.core.ui.components.badge

import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
fun AppBadge(
    count: Int,
    containerColor: Color = AppTheme.colors.secondaryContainer,
    contentColor: Color = AppTheme.colors.onSecondaryContainer
) {
    Badge(
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = count.toString()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBadgePreview() {
    PreviewLightTheme {
        AppBadge(
            count = 1
        )
    }
}