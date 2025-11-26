package com.egormelnikoff.schedulerutmiit.app.widget.ui.theme


import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.egormelnikoff.schedulerutmiit.ui.theme.color.Black
import com.egormelnikoff.schedulerutmiit.ui.theme.color.Blue
import com.egormelnikoff.schedulerutmiit.ui.theme.color.DarkGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.color.Grey
import com.egormelnikoff.schedulerutmiit.ui.theme.color.LightGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.color.NeutralSecondaryContainer
import com.egormelnikoff.schedulerutmiit.ui.theme.color.NeutralSecondaryContainerDark
import com.egormelnikoff.schedulerutmiit.ui.theme.color.Red
import com.egormelnikoff.schedulerutmiit.ui.theme.color.White

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = White,

    background = White,
    onBackground = Black,

    secondaryContainer = NeutralSecondaryContainer,
    onSecondaryContainer = Grey,

    outline = LightGrey,
    error = Red
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = White,

    background = DarkGrey,
    onBackground = White,

    secondaryContainer = NeutralSecondaryContainerDark,
    onSecondaryContainer = LightGrey,

    outline = LightGrey,
    error = Red
)

@Composable
fun ScheduleGlanceTheme(
    content: @Composable () -> Unit
) {
    GlanceTheme(
        colors = ColorProviders(
            dark = DarkColorScheme,
            light = LightColorScheme
        ),
        content = content
    )
}