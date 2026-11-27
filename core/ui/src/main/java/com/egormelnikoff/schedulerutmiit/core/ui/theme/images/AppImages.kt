package com.egormelnikoff.schedulerutmiit.core.ui.theme.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.egormelnikoff.schedulerutmiit.core.ui.R

@Immutable
class AppImages internal constructor() {
    val advice: Painter
        @Composable get() = painterResource(R.drawable.advice)
    val commentAndTag: Painter
        @Composable get() = painterResource(R.drawable.comment_and_tag)
    val day: Painter
        @Composable get() = painterResource(R.drawable.day)
    val evening: Painter
        @Composable get() = painterResource(R.drawable.evening)
    val night: Painter
        @Composable get() = painterResource(R.drawable.night)
    val schedules: Painter
        @Composable get() = painterResource(R.drawable.schedules)
    val widget: Painter
        @Composable get() = painterResource(R.drawable.widget)
}

val LocalAppImages = staticCompositionLocalOf { AppImages() }
