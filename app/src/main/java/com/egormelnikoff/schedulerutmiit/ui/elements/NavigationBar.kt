package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Routes

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
    barItems: Array<BarItem>
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowRadius by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 8.dp,
        animationSpec = tween(durationMillis = 150)
    )

    val shadowOffsetX by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 2.dp,
        animationSpec = tween(durationMillis = 150)
    )
    val shadowOffsetY by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 2.dp,
        animationSpec = tween(durationMillis = 150)
    )

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
                    interactionSource = interactionSource,
                    indication = null
                ) {}
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = shadowRadius,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        offset = DpOffset(shadowOffsetX, shadowOffsetY)
                    )
                )
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
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = barItem.title,
            fontSize = 8.sp,
            fontWeight = if (isSelected) FontWeight.Bold
            else FontWeight.Normal,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}