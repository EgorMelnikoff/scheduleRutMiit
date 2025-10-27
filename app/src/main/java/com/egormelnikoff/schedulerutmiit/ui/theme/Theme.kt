package com.egormelnikoff.schedulerutmiit.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings

@Composable
fun ScheduleRutMiitTheme(
    appSettings: AppSettings?,
    content: @Composable () -> Unit
) {
    val currentPrimary = primaryColors[appSettings?.decorColorIndex ?: 0]
    val currentBackground = backgroundColors[appSettings?.decorColorIndex ?: 0]
    val currentSecondaryContainer = secondaryContainerColors[appSettings?.decorColorIndex ?: 0]

    val lightColorScheme = lightColorScheme(
        primary = currentPrimary,
        onPrimary = White,

        background = currentBackground.first,
        onBackground = Black,

        secondaryContainer = currentSecondaryContainer.first,
        onSecondaryContainer = Grey,

        outline = if (appSettings?.decorColorIndex == 0) LightGrey
        else currentPrimary,

        error = Red
    )
    val darkColorScheme = darkColorScheme(
        primary = currentPrimary,
        onPrimary = White,

        background = currentBackground.second,
        onBackground = White,

        secondaryContainer = currentSecondaryContainer.second,
        onSecondaryContainer = LightGrey,

        outline = if (appSettings?.decorColorIndex == 0) LightGrey
        else currentPrimary,

        error = Red
    )

    val darkTheme = when (appSettings?.theme) {
        "dark" -> true
        "light" -> false
        else -> {
            isSystemInDarkTheme()
        }
    }
    val colorScheme = if (darkTheme) darkColorScheme
    else lightColorScheme

    val animation = SpringSpec<Color>(stiffness = Spring.StiffnessMediumLow)
    val primary by animateColorAsState(colorScheme.primary, animation)
    val onPrimary by animateColorAsState(colorScheme.onPrimary, animation)
    val background by animateColorAsState(colorScheme.background, animation)
    val onBackground by animateColorAsState(colorScheme.onBackground, animation)
    val secondaryContainer by animateColorAsState(colorScheme.secondaryContainer, animation)
    val onSecondaryContainer by animateColorAsState(colorScheme.onSecondaryContainer, animation)
    val outline by animateColorAsState(colorScheme.outline, animation)
    val error by animateColorAsState(colorScheme.error, animation)

    val animatedColorScheme = colorScheme.copy(
        background = background,
        onBackground = onBackground,

        primary = primary,
        onPrimary = onPrimary,

        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,

        outline = outline,
        error = error,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
