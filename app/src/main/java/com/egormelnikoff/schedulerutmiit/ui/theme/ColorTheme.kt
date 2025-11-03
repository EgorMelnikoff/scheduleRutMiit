package com.egormelnikoff.schedulerutmiit.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color


data class Theme(
    val animation: SpringSpec<Color>,
    val light: ColorTheme,
    val dark: ColorTheme
) {
    @Composable
    fun toColorScheme(isDarkTheme: Boolean): ColorScheme {
        val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
        val theme = if (isDarkTheme) this.dark else this.light

        val primary by animateColorAsState(theme.primary, animation)
        val onPrimary by animateColorAsState(theme.onPrimary, animation)
        val background by animateColorAsState(theme.background, animation)
        val onBackground by animateColorAsState(theme.onBackground, animation)
        val primaryContainer by animateColorAsState(theme.primaryContainer, animation)
        val onPrimaryContainer by animateColorAsState(theme.onPrimaryContainer, animation)
        val secondaryContainer by animateColorAsState(theme.secondaryContainer, animation)
        val onSecondaryContainer by animateColorAsState(theme.onSecondaryContainer, animation)
        val outline by animateColorAsState(theme.outline, animation)
        val error by animateColorAsState(theme.error, animation)

        return colorScheme.copy(
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
}

data class ColorTheme(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackground: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val outline: Color,
    val error: Color
)

fun createLightTheme(
    primaryColor: Color,
    backgroundColor: Color,
    primaryContainerColor: Color,
    secondaryContainerColor: Color
) = ColorTheme(
    primary = primaryColor,
    onPrimary = White,
    background = backgroundColor,
    onBackground = Black,
    primaryContainer = primaryContainerColor,
    onPrimaryContainer = Grey,
    secondaryContainer = secondaryContainerColor,
    onSecondaryContainer = Grey,
    outline = LightGrey.copy(alpha = 0.7f),
    error = Red
)

fun createDarkTheme(
    primaryColor: Color,
    backgroundColor: Color,
    primaryContainerColor: Color,
    secondaryContainerColor: Color
) = ColorTheme(
    primary = primaryColor,
    background = backgroundColor,
    outline = LightGrey.copy(alpha = 0.5f),
    onPrimary = White,
    onBackground = White,
    primaryContainer = primaryContainerColor,
    onPrimaryContainer = Grey,
    secondaryContainer = secondaryContainerColor,
    onSecondaryContainer = LightGrey,
    error = Red
)