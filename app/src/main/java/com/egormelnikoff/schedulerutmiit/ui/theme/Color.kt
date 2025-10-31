package com.egormelnikoff.schedulerutmiit.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Grey = Color(0xFF595959)
val DarkGrey = Color(0xFF111111)
val LightGrey = Color(0xFFb5b3b3)
val NeutralSecondary = Color(0xFFEEEEEE)
val NeutralSecondaryDark = Color(0xFF212121)

val Red = Color(0xFFcc584c)
val Orange = Color(0xFFe78450)
val Yellow = Color(0xFFd0a844)
val Green = Color(0xFF3e9682)
val LightBlue = Color(0xFF3c98c4)
val Blue = Color(0xFF4f74e0)
val Violet = Color(0xFF9848c2)
val Pink = Color(0xFFdc53c1)

val colors = mapOf(
    0 to LightGrey,
    4 to Green,
    2 to Orange,
    6 to Blue,
    1 to Red,
    7 to Violet,
    3 to Yellow,
    8 to Pink,
    5 to LightBlue
)

fun getColorByIndex(
    index: Int?,
    defaultColor: Color = Color.Unspecified
): Color {
    if (index == 0 || index == null) return defaultColor
    return colors[index] ?: Color.Unspecified
}

val animation: SpringSpec<Color> = SpringSpec(stiffness = Spring.StiffnessMediumLow)

val defaultColorTheme = Theme(
    animation = animation,
    light = ColorTheme(
        primary = Blue,
        onPrimary = White,
        background = White,
        onBackground = Black,
        secondaryContainer = NeutralSecondary,
        onSecondaryContainer = Grey,
        outline = LightGrey,
        error = Red
    ),
    dark = ColorTheme(
        primary = Blue,
        onPrimary = White,
        background = DarkGrey,
        onBackground = White,
        secondaryContainer = NeutralSecondaryDark,
        onSecondaryContainer = LightGrey,
        outline = LightGrey,
        error = Red
    )
)

val colorThemes = mapOf(
    0 to defaultColorTheme, //Neutral
    1 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Red,
            backgroundColor = Color(0xFFfcdcd9),
            secondaryContainerColor = Color(0xFFfcc3bd),
            outlineColor = Red
        ),
        dark = createDarkTheme(
            primaryColor = Red,
            backgroundColor = Color(0xFF2e0000),
            secondaryContainerColor = Color(0xFF420101),
            outlineColor = Red
        )
    ), //Red
    2 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Orange,
            backgroundColor = Color(0xFFffd8c4),
            secondaryContainerColor = Color(0xFFfac6ac),
            outlineColor = Orange
        ),
        dark = createDarkTheme(
            primaryColor = Orange,
            backgroundColor = Color(0xFF260e01),
            secondaryContainerColor = Color(0xFF421801),
            outlineColor = Orange
        )
    ), //Orange
    3 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Yellow,
            backgroundColor = Color(0xFFfff3d4),
            secondaryContainerColor = Color(0xFFf7e4b2),
            outlineColor = Yellow
        ),
        dark = createDarkTheme(
            primaryColor = Yellow,
            backgroundColor = Color(0xFF472e00),
            secondaryContainerColor = Color(0xFF6b4601),
            outlineColor = Yellow
        )
    ), //Yellow
    4 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Green,
            backgroundColor = Color(0xFFe1fce1),
            secondaryContainerColor = Color(0xFFcbf2c9),
            outlineColor = Green
        ),
        dark = createDarkTheme(
            primaryColor = Green,
            backgroundColor = Color(0xFF001f18),
            secondaryContainerColor = Color(0xFF013629),
            outlineColor = Green
        )
    ), //Green
    5 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = LightBlue,
            backgroundColor = Color(0xFFdcf2fc),
            secondaryContainerColor = Color(0xFFc0e9fc),
            outlineColor = LightBlue
        ),
        dark = createDarkTheme(
            primaryColor = LightBlue,
            backgroundColor = Color(0xFF01131c),
            secondaryContainerColor = Color(0xFF01273b),
            outlineColor = LightBlue
        )
    ), //LightBlue
    6 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Blue,
            backgroundColor = Color(0xFFdee6ff),
            secondaryContainerColor = Color(0xFFcdd7fa),
            outlineColor = Blue
        ),
        dark = createDarkTheme(
            primaryColor = Blue,
            backgroundColor = Color(0xFF010e24),
            secondaryContainerColor = Color(0xFF011840),
            outlineColor = Blue
        )
    ), //Blue
    7 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Violet,
            backgroundColor = Color(0xFFeed7fa),
            secondaryContainerColor = Color(0xFFeac4ff),
            outlineColor = Violet
        ),
        dark = createDarkTheme(
            primaryColor = Violet,
            backgroundColor = Color(0xFF180024),
            secondaryContainerColor = Color(0xFF290040),
            outlineColor = Violet
        )
    ), //Violet
    8 to Theme(
        animation = animation,
        light = createLightTheme(
            primaryColor = Pink,
            backgroundColor = Color(0xFFffd4f6),
            secondaryContainerColor = Color(0xFFffbff2),
            outlineColor = Pink
        ),
        dark = createDarkTheme(
            primaryColor = Pink,
            backgroundColor = Color(0xFF21001b),
            secondaryContainerColor = Color(0xFF450038),
            outlineColor = Pink
        )
    ) //Pink
)