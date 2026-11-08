package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ReviewScreenStatusBarEffect(
    overlappedFraction: Float,
    isDarkTheme: Boolean
) {
    val view = LocalView.current

    val window = remember(view) {
        (view.context as? Activity)?.window
    }

    val insetsController = remember(view, window) {
        window?.let {
            WindowCompat.getInsetsController(it, view)
        }
    }

    SideEffect {
        insetsController?.isAppearanceLightStatusBars = !isDarkTheme && overlappedFraction > 0.01f
    }

    DisposableEffect(isDarkTheme) {
        onDispose {
            insetsController?.isAppearanceLightStatusBars = !isDarkTheme
        }
    }
}