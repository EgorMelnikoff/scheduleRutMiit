package com.egormelnikoff.schedulerutmiit.core.ui.containers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun AnimatedAlert(
    paddingVertical: Dp = AppTheme.paddings.padding8,
    paddingHorizontal: Dp = AppTheme.paddings.padding16,
    isHidden: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = !isHidden,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = paddingVertical,
                horizontal = paddingHorizontal
            )
        ) {
            content.invoke()
        }
    }
}