package com.egormelnikoff.schedulerutmiit.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.ui.theme.color.amoledColorScheme
import com.egormelnikoff.schedulerutmiit.ui.theme.color.animation
import com.egormelnikoff.schedulerutmiit.ui.theme.color.defaultTheme
import com.egormelnikoff.schedulerutmiit.ui.theme.color.themes

@Composable
fun ScheduleRutMiitTheme(
    theme: String,
    decorColorIndex: Int,
    content: @Composable () -> Unit
) {
    val isDarkTheme = theme.isDarkTheme()
    val colorScheme = getCurrentColorScheme(
        isDarkTheme = isDarkTheme,
        isUsedAmoledTheme = theme == "amoled",
        decorColorIndex = decorColorIndex
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.isNavigationBarContrastEnforced = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun String.isDarkTheme(): Boolean {
    return when (this) {
        "dark", "amoled" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
}


@Composable
fun getCurrentColorScheme(
    isDarkTheme: Boolean,
    isUsedAmoledTheme: Boolean,
    decorColorIndex: Int
): ColorScheme {
    val currentColorTheme = themes[decorColorIndex] ?: defaultTheme

    val colorTheme = when {
        isUsedAmoledTheme -> amoledColorScheme.copy(
            primary = currentColorTheme.dark.primary
        )
        isDarkTheme -> currentColorTheme.dark
        else -> currentColorTheme.light
    }

    val primary by animateColorAsState(colorTheme.primary, animation)
    val onPrimary by animateColorAsState(colorTheme.onPrimary, animation)
    val background by animateColorAsState(colorTheme.background, animation)
    val onBackground by animateColorAsState(colorTheme.onBackground, animation)
    val primaryContainer by animateColorAsState(colorTheme.primaryContainer, animation)
    val onPrimaryContainer by animateColorAsState(colorTheme.onPrimaryContainer, animation)
    val secondaryContainer by animateColorAsState(colorTheme.secondaryContainer, animation)
    val onSecondaryContainer by animateColorAsState(colorTheme.onSecondaryContainer, animation)
    val outline by animateColorAsState(colorTheme.outline, animation)
    val error by animateColorAsState(colorTheme.error, animation)

    val baseColorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    return baseColorScheme.copy(
        background = background,
        onBackground = onBackground,

        primary = primary,
        onPrimary = onPrimary,

        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,

        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,

        outline = outline,
        error = error
    )
}