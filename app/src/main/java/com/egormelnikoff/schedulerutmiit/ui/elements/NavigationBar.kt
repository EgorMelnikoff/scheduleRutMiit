package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.state.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.theme.color.Grey
import com.egormelnikoff.schedulerutmiit.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import kotlinx.coroutines.launch

data class BarItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: Route.Page,
    val onClick: (() -> Unit)?
)

@Composable
fun CustomNavigationBar(
    appUiState: AppUiState,
    scheduleState: ScheduleState,
    scheduleUiState: ScheduleUiState?,
    appSettings: AppSettings
) {
    val barItems = barItems(
        onScheduleClick = {
            appUiState.scope.launch {
                when {
                    scheduleUiState != null && scheduleState.currentNamedScheduleData?.scheduleData?.schedulePagerData != null && appSettings.calendarView -> {
                        scheduleUiState.onSelectDate(
                            scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.defaultDate
                        )
                        scheduleUiState.pagerWeeksState.animateScrollToPage(
                            scheduleState.currentNamedScheduleData.scheduleData.schedulePagerData.weeksStartIndex
                        )
                    }

                    scheduleUiState != null && !appSettings.calendarView -> {
                        scheduleUiState.scheduleListState.animateScrollToItem(0)
                    }
                }
            }
        },
        onNewsClick = {
            appUiState.scope.launch {
                appUiState.newsListState.animateScrollToItem(0)
            }
        },
        onSettingsClick = {
            appUiState.scope.launch {
                appUiState.settingsListState.animateScrollToItem(0)
            }
        }
    )

    var selectedIndex by remember { mutableIntStateOf(1) }

    val indicatorOffset by animateDpAsState(
        targetValue = 68.dp * selectedIndex,
        animationSpec = tween(200, easing = LinearOutSlowInEasing)
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
        Box(
            modifier = Modifier
                .clickable(
                    enabled = false
                ) {}
                .let {
                    if (appSettings.theme.isDarkTheme()) {
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .offset(x = indicatorOffset)
                    .width(68.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f))
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                barItems.forEachIndexed { index, barItem ->
                    CustomNavigationItem(
                        barItem = barItem,
                        isSelected = appUiState.appBackStack.lastPage() == barItem.route,
                        onClick = {
                            if (barItem.route == appUiState.appBackStack.lastPage()) {
                                if (appUiState.appBackStack.last() is Route.Dialog) {
                                    appUiState.appBackStack.onBack()
                                } else {
                                    barItem.onClick?.invoke()
                                }
                            } else {
                                appUiState.appBackStack.navigateToPage(barItem.route)
                                selectedIndex = index
                            }
                        }
                    )
                }
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
            .padding(8.dp)
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


@Composable
fun barItems(
    onScheduleClick: () -> Unit,
    onNewsClick: () -> Unit,
    onSettingsClick: () -> Unit
): Array<BarItem> {
    return arrayOf(
        BarItem(
            title = LocalContext.current.getString(R.string.review),
            icon = ImageVector.vectorResource(R.drawable.review),
            selectedIcon = ImageVector.vectorResource(R.drawable.review_fill),
            route = Route.Page.Review,
            onClick = null
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.schedule),
            icon = ImageVector.vectorResource(R.drawable.schedule),
            selectedIcon = ImageVector.vectorResource(R.drawable.schedule_fill),
            route = Route.Page.Schedule,
            onClick = onScheduleClick
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.news),
            icon = ImageVector.vectorResource(R.drawable.news),
            selectedIcon = ImageVector.vectorResource(R.drawable.news_fill),
            route = Route.Page.NewsList,
            onClick = onNewsClick
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.settings),
            icon = ImageVector.vectorResource(R.drawable.settings),
            selectedIcon = ImageVector.vectorResource(R.drawable.settings_fill),
            route = Route.Page.Settings,
            onClick = onSettingsClick
        )
    )
}