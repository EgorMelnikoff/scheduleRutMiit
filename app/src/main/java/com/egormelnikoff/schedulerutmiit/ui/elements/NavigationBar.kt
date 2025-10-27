package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Routes
import com.egormelnikoff.schedulerutmiit.ui.theme.Grey

data class BarItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: Routes,
    val onClick: (() -> Unit)?
)

@Composable
fun CustomNavigationBar(
    appBackStack: AppBackStack<Routes.Schedule>,
    barItems: Array<BarItem>,
    theme: String
) {
    val darkTheme = when (theme) {
        "dark" -> true
        "light" -> false
        else -> {
            isSystemInDarkTheme()
        }
    }
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
                    .calculateBottomPadding() + 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    enabled = false
                ) {}
                .let {
                    if (darkTheme) {
                        it.border(
                            width = 0.5.dp,
                            shape = CircleShape,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                                )
                            )
                        )
                    } else {
                        it.dropShadow(
                            shape = CircleShape,
                            shadow = Shadow(
                                radius = 8.dp,
                                color = Grey.copy(0.5f),
                                offset = DpOffset(2.dp, 2.dp)
                            )
                        )
                    }
                }
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            barItems.forEach { barItem ->
                CustomNavigationItem(
                    barItem = barItem,
                    isSelected = appBackStack.lastPage() == barItem.route,
                    onClick = {
                        if (barItem.route == appBackStack.lastPage()) {
                            barItem.onClick?.invoke()
                        } else {
                            appBackStack.navigateToPage(barItem.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomNavigationItem(
    barItem: BarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(scale)
            .width(52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = if (isSelected) barItem.selectedIcon else barItem.icon,
            contentDescription = barItem.title,
            tint = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = barItem.title,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSecondaryContainer,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}