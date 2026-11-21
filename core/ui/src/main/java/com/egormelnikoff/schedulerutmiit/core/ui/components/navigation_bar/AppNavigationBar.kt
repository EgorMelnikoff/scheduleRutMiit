package com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Grey

data class BarItem(
    @param:StringRes
    val title: Int,
    @param:DrawableRes
    val iconRes: Int,
    @param:DrawableRes
    val selectedIconRes: Int? = null
)

@Composable
fun AppNavigationBar(
    barItems: @Composable RowScope.() -> Unit,
    isDarkTheme: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        AppTheme.colors.background
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(horizontal = AppTheme.paddings.padding24)
            .padding(
                top = AppTheme.paddings.padding12,
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + AppTheme.paddings.padding8
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    enabled = false
                ) {}
                .let {
                    if (isDarkTheme) {
                        it.border(
                            width = 0.5.dp,
                            shape = CircleShape,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    AppTheme.colors.onSecondaryContainer.copy(alpha = 0.2f),
                                    Color.Transparent,
                                    AppTheme.colors.onSecondaryContainer.copy(alpha = 0.1f)
                                )
                            )
                        )
                    } else {
                        it.dropShadow(
                            shape = CircleShape,
                            shadow = Shadow(
                                radius = 8.dp,
                                color = Grey.copy(0.4f),
                                offset = DpOffset(2.dp, 2.dp)
                            )
                        )
                    }
                }
                .background(
                    color = AppTheme.colors.background,
                    shape = CircleShape
                )
                .height(IntrinsicSize.Min)
                .padding(AppTheme.paddings.padding4),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                barItems()
            }
        }
    }
}