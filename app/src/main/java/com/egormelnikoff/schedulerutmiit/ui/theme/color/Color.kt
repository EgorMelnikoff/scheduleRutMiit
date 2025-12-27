package com.egormelnikoff.schedulerutmiit.ui.theme.color

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.graphics.Color

val White = Color(0xFFFFFFFF)
val LightGrey = Color(0xFFb5b3b3)
val Grey = Color(0xFF595959)
val DarkGrey = Color(0xFF111111)
val Black = Color(0xFF000000)
val NeutralPrimaryContainer = Color(0xFFe0e0e0)
val NeutralPrimaryContainerDark = Color(0xFF2e2e2e)
val NeutralSecondaryContainer = Color(0xFFEEEEEE)
val NeutralSecondaryContainerDark = Color(0xFF212121)
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

val animation: SpringSpec<Color> = SpringSpec(stiffness = Spring.StiffnessMedium)

val defaultTheme = Theme(
    animation = animation, light = createLightColorTheme(
        primaryColor = Blue,
        backgroundColor = White,
        primaryContainerColor = NeutralPrimaryContainer,
        secondaryContainerColor = NeutralSecondaryContainer
    ), dark = createDarkColorTheme(
        primaryColor = Blue,
        backgroundColor = DarkGrey,
        primaryContainerColor = NeutralPrimaryContainerDark,
        secondaryContainerColor = NeutralSecondaryContainerDark
    )
)

val themes = mapOf(
    //Neutral
    0 to defaultTheme,
    //Red
    1 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Red,
            backgroundColor = Color(0xFFfcdcd9),
            primaryContainerColor = Color(0xFFf5b8b0),
            secondaryContainerColor = Color(0xFFfcc3bd)
        ), dark = createDarkColorTheme(
            primaryColor = Red,
            backgroundColor = Color(0xFF2e0000),
            primaryContainerColor = Color(0xFF4f0000),
            secondaryContainerColor = Color(0xFF420101)
        )
    ),
    //Orange
    2 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Orange,
            backgroundColor = Color(0xFFffd8c4),
            primaryContainerColor = Color(0xFFf2ba9b),
            secondaryContainerColor = Color(0xFFfac6ac)
        ), dark = createDarkColorTheme(
            primaryColor = Orange,
            backgroundColor = Color(0xFF260e01),
            primaryContainerColor = Color(0xFF5e2100),
            secondaryContainerColor = Color(0xFF421801)
        )
    ),
    //Yellow
    3 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Yellow,
            backgroundColor = Color(0xFFfff3d4),
            primaryContainerColor = Color(0xFFf0d8a1),
            secondaryContainerColor = Color(0xFFf7e4b2)
        ), dark = createDarkColorTheme(
            primaryColor = Yellow,
            backgroundColor = Color(0xFF472e00),
            primaryContainerColor = Color(0xFF7a5000),
            secondaryContainerColor = Color(0xFF6b4601)
        )
    ),
    //Green
    4 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Green,
            backgroundColor = Color(0xFFe1fce1),
            primaryContainerColor = Color(0xFFb8e3b6),
            secondaryContainerColor = Color(0xFFcbf2c9)
        ), dark = createDarkColorTheme(
            primaryColor = Green,
            backgroundColor = Color(0xFF001f18),
            primaryContainerColor = Color(0xFF004534),
            secondaryContainerColor = Color(0xFF013629)
        )
    ),
    //LightBlue
    5 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = LightBlue,
            backgroundColor = Color(0xFFdcf2fc),
            primaryContainerColor = Color(0xFFa2e0fc),
            secondaryContainerColor = Color(0xFFc0e9fc)
        ), dark = createDarkColorTheme(
            primaryColor = LightBlue,
            backgroundColor = Color(0xFF01131c),
            primaryContainerColor = Color(0xFF003a59),
            secondaryContainerColor = Color(0xFF01304a)
        )
    ),
    //Blue
    6 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Blue,
            backgroundColor = Color(0xFFdee6ff),
            primaryContainerColor = Color(0xFFb9c7fa),
            secondaryContainerColor = Color(0xFFcdd7fa)
        ), dark = createDarkColorTheme(
            primaryColor = Blue,
            backgroundColor = Color(0xFF010e24),
            primaryContainerColor = Color(0xFF011e52),
            secondaryContainerColor = Color(0xFF011840)
        )
    ),
    //Violet
    7 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Violet,
            backgroundColor = Color(0xFFeed7fa),
            primaryContainerColor = Color(0xFFe1b1fa),
            secondaryContainerColor = Color(0xFFeac4ff)
        ), dark = createDarkColorTheme(
            primaryColor = Violet,
            backgroundColor = Color(0xFF180024),
            primaryContainerColor = Color(0xFF340152),
            secondaryContainerColor = Color(0xFF290040)
        )
    ),
    //Pink
    8 to Theme(
        animation = animation, light = createLightColorTheme(
            primaryColor = Pink,
            backgroundColor = Color(0xFFffd4f6),
            primaryContainerColor = Color(0xFFfca9ec),
            secondaryContainerColor = Color(0xFFffbff2)
        ), dark = createDarkColorTheme(
            primaryColor = Pink,
            backgroundColor = Color(0xFF21001b),
            primaryContainerColor = Color(0xFF590049),
            secondaryContainerColor = Color(0xFF450038)
        )
    )
)