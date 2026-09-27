package com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Grey
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Red

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
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(horizontal = 24.dp)
            .padding(
                top = 12.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 8.dp
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
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
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
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
                .height(IntrinsicSize.Min)
                .padding(4.dp),
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