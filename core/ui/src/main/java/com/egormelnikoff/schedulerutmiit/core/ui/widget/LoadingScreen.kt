package com.egormelnikoff.schedulerutmiit.core.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun LoadingScreen(
    paddingTop: Dp = AppTheme.paddings.none,
    paddingBottom: Dp = AppTheme.paddings.none
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(top = paddingTop, bottom = paddingBottom)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center),
            color = AppTheme.colors.primary
        )
    }
}