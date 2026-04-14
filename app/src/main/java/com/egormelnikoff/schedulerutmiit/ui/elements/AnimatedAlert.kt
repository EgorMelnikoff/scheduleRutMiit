package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedAlert(
    paddingVertical: Dp = 8.dp,
    paddingHorizontal: Dp = 16.dp,
    isHidden: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = !isHidden,
        enter = expandVertically(),
        exit = shrinkVertically()
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