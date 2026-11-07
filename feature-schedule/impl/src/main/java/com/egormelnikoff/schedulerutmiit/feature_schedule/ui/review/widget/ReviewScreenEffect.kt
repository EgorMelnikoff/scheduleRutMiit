package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState

@Composable
fun ReviewScreenEffect(
    overlappedFraction: Float,
    reviewState: ReviewState,
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

    val isDarkTheme = reviewState.theme.isDarkTheme()

    SideEffect {
        insetsController?.isAppearanceLightStatusBars = overlappedFraction > 0.01f
    }

    DisposableEffect(Unit) {
        onDispose {
            insetsController?.isAppearanceLightStatusBars = !isDarkTheme
        }
    }
}