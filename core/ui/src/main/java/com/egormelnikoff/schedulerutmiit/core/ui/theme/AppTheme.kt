package com.egormelnikoff.schedulerutmiit.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.egormelnikoff.schedulerutmiit.core.ui.theme.paddings.AppPaddings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.paddings.LocalAppPaddings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.radius.AppRadius
import com.egormelnikoff.schedulerutmiit.core.ui.theme.radius.LocalAppRadius
import com.egormelnikoff.schedulerutmiit.core.ui.theme.shapes.AppShapes
import com.egormelnikoff.schedulerutmiit.core.ui.theme.shapes.LocalAppShapes
import com.egormelnikoff.schedulerutmiit.core.ui.theme.typography.AppTypography
import com.egormelnikoff.schedulerutmiit.core.ui.theme.typography.LocalAppTypography

object AppTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val paddings: AppPaddings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppPaddings.current

    val radius: AppRadius
        @Composable
        @ReadOnlyComposable
        get() = LocalAppRadius.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current
}
