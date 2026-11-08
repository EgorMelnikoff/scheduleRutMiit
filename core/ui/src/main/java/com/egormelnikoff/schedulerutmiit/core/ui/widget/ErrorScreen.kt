package com.egormelnikoff.schedulerutmiit.core.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun ErrorScreen(
    title: String? = null,
    subtitle: String? = null,
    button: @Composable (() -> Unit)? = null,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .padding(
                start = AppTheme.paddings.padding32,
                end = AppTheme.paddings.padding32,
                top = paddingTop,
                bottom = paddingBottom
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
        ) {
            title?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colors.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            subtitle?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
            }
            button?.invoke()
        }
    }
}