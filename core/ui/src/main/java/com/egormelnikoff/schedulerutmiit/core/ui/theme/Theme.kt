package com.egormelnikoff.schedulerutmiit.core.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ThemePreferences
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.amoledColorScheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.animation
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.defaultTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.themes
import com.egormelnikoff.schedulerutmiit.core.ui.theme.paddings.AppPaddings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.paddings.LocalAppPaddings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.radius.AppRadius
import com.egormelnikoff.schedulerutmiit.core.ui.theme.radius.LocalAppRadius
import com.egormelnikoff.schedulerutmiit.core.ui.theme.shapes.AppShapes
import com.egormelnikoff.schedulerutmiit.core.ui.theme.shapes.LocalAppShapes
import com.egormelnikoff.schedulerutmiit.core.ui.theme.typography.AppTypography
import com.egormelnikoff.schedulerutmiit.core.ui.theme.typography.LocalAppTypography

@Composable
fun ScheduleRutMiitTheme(
    themePreferences: ThemePreferences,
    content: @Composable () -> Unit
) {
    val isDarkTheme = themePreferences.theme.isDarkTheme()
    val colorScheme = themePreferences.getCurrentColorScheme(
        isDarkTheme = isDarkTheme
    )

    val view = LocalView.current
    val configuration = LocalConfiguration.current

    if (!view.isInEditMode) {
        DisposableEffect(isDarkTheme, configuration.orientation) {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            window.isNavigationBarContrastEnforced = false

            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme

            onDispose {}
        }
    }


    val paddings = AppPaddings()
    val radius = AppRadius()
    val shapes = AppShapes()
    val typography = AppTypography()

    CompositionLocalProvider(
        LocalAppPaddings provides paddings,
        LocalAppRadius provides radius,
        LocalAppShapes provides shapes,
        LocalAppTypography provides typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(
                headlineLarge = typography.headlineLarge,
                bodyLarge = typography.bodyLarge,
                bodyMedium = typography.bodyMedium,
                bodySmall = typography.bodySmall,
                titleLarge = typography.titleLarge,
                titleMedium = typography.bodyMedium,
                titleSmall = typography.titleSmall
            ),
            shapes = Shapes(
                extraSmall = shapes.extraSmall,
                small = shapes.small,
                medium = shapes.medium,
                large = shapes.large,
                extraLarge = shapes.extraLarge
            ),

            content = content
        )
    }
}

@Composable
fun PreviewLightTheme(
    content: @Composable () -> Unit
) {
    ScheduleRutMiitTheme(
        themePreferences = ThemePreferences(
            Theme.LIGHT,
            usedAmoled = false,
            decorColorIndex = 0
        )
    ) {
        content.invoke()
    }
}

@Composable
fun Theme.isDarkTheme() = when (this) {
    Theme.DARK -> true
    Theme.LIGHT -> false
    else -> isSystemInDarkTheme()
}

@Composable
fun ThemePreferences.getCurrentColorScheme(
    isDarkTheme: Boolean
): ColorScheme {
    val currentColorTheme = themes[this.decorColorIndex] ?: defaultTheme

    val colorTheme = when {
        isDarkTheme && this.usedAmoled -> amoledColorScheme.copy(
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