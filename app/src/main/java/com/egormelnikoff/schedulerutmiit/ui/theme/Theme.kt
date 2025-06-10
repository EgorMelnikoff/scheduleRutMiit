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
import com.egormelnikoff.schedulerutmiit.AppSettings


@Composable
fun ScheduleRutMiitTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit
) {
    val primaryColors = arrayOf(
        Pair(lightThemeBlue, darkThemeBlue),
        Pair(lightThemeRed, darkThemeRed),
        Pair(lightThemeOrange, darkThemeOrange),
        Pair(lightThemeYellow, darkThemeYellow),
        Pair(lightThemeGreen, darkThemeGreen),
        Pair(lightThemeLightBlue, darkThemeLightBlue),
        Pair(lightThemeBlue, darkThemeBlue),
        Pair(lightThemeViolet, darkThemeViolet),
        Pair(lightThemePink, darkThemePink),
    )


    val backGroundColors = arrayOf(
        Pair(White, darkThemeNeutralSurface),
        Pair(lightThemeRedBackground, darkThemeRedBackground),
        Pair(lightThemeOrangeBackground, darkThemeOrangeBackground),
        Pair(lightThemeYellowBackground, darkThemeYellowBackground),
        Pair(lightThemeGreenBackground, darkThemeGreenBackground),
        Pair(lightThemeLightBlueBackground, darkThemeLightBlueBackground),
        Pair(lightThemeBlueBackground, darkThemeBlueBackground),
        Pair(lightThemeVioletBackground, darkThemeVioletBackground),
        Pair(lightThemePinkBackground, darkThemePinkBackground),
    )

    val surfaceColors = arrayOf(
        Pair(lightThemeNeutralSurface, DarkGrey),
        Pair(lightThemeRedSurface, darkThemeRedSurface),
        Pair(lightThemeOrangeSurface, darkThemeOrangeSurface),
        Pair(lightThemeYellowSurface, darkThemeYellowSurface),
        Pair(lightThemeGreenSurface, darkThemeGreenSurface),
        Pair(lightThemeLightBlueSurface, darkThemeLightBlueSurface),
        Pair(lightThemeBlueSurface, darkThemeBlueSurface),
        Pair(lightThemeVioletSurface, darkThemeVioletSurface),
        Pair(lightThemePinkSurface, darkThemePinkSurface),
    )

    val currentPrimary = primaryColors[appSettings.decorColorIndex]
    val currentBackground = backGroundColors[appSettings.decorColorIndex]
    val currentSurface = surfaceColors[appSettings.decorColorIndex]


    val lightColorScheme = lightColorScheme(
        background = currentBackground.first,
        onBackground = Black,

        primary = currentPrimary.first,
        onPrimary = White,

        surface = currentSurface.first,
        onSurface = Grey,
        onSurfaceVariant = Grey, //


        outline = LightGrey,

        surfaceContainerLow = darkThemeGreen,
        surfaceContainer = darkThemeYellow,
        surfaceContainerHigh = darkThemeRed

    )


    val darkColorScheme = darkColorScheme(
        background = currentBackground.second,
        onBackground = White,

        primary = currentPrimary.second,
        onPrimary = White,


        surface = currentSurface.second,
        onSurface = LightGrey,
        onSurfaceVariant = LightGrey, //

        outline = LightGrey,

        surfaceContainerLow = lightThemeGreen,
        surfaceContainer = lightThemeYellow,
        surfaceContainerHigh = lightThemeRed

    )


    val colorScheme = when {
        appSettings.theme == "light" -> lightColorScheme
        appSettings.theme == "dark" -> darkColorScheme
        isSystemInDarkTheme() -> darkColorScheme
        else -> lightColorScheme
    }

    val darkTheme = when (appSettings.theme) {
        "dark" -> true
        "light" -> false
        else -> {
            isSystemInDarkTheme()
        }
    }



    val animation = SpringSpec<Color>(stiffness = Spring.StiffnessMediumLow)


    val background by animateColorAsState(colorScheme.background, animation)
    val onBackground by animateColorAsState(colorScheme.onBackground, animation)
    val primary by animateColorAsState(colorScheme.primary, animation)
    val onPrimary by animateColorAsState(colorScheme.onPrimary, animation)
    val surface by animateColorAsState(colorScheme.surface, animation)
    val onSurface by animateColorAsState(colorScheme.onSurface, animation)
    val onSurfaceVariant by animateColorAsState(colorScheme.onSurfaceVariant, animation)
    val outline by animateColorAsState(colorScheme.outline, animation)
    val error by animateColorAsState(colorScheme.error, animation)


    val animatedColors = colorScheme.copy(
        background = background,
        onBackground = onBackground,
        primary = primary,
        onPrimary = onPrimary,
        surface = surface,
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        error = error,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect  {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = animatedColors,
        typography = Typography,
        content = content
    )

}
