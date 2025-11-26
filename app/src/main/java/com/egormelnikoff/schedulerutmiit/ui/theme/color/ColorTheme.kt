package com.egormelnikoff.schedulerutmiit.ui.theme.color

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.graphics.Color

data class Theme(
    val animation: SpringSpec<Color>,
    val light: ColorTheme,
    val dark: ColorTheme
)

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

val amoledColorScheme = ColorTheme(
    primary = Blue,
    background = Black,
    outline = LightGrey.copy(alpha = 0.5f),
    onPrimary = White,
    onBackground = White,
    primaryContainer = NeutralPrimaryContainerDark,
    onPrimaryContainer = Grey,
    secondaryContainer = DarkGrey,
    onSecondaryContainer = LightGrey,
    error = Red
)

fun createLightColorTheme(
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

fun createDarkColorTheme(
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
