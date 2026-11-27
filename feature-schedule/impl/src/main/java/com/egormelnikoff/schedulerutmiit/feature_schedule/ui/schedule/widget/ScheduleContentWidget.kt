package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Custom
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.components.pull_to_refresh.AppPullToRefreshBox
import com.egormelnikoff.schedulerutmiit.core.ui.containers.AnimatedAlert
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.R
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model.ScheduleVo

@Composable
internal fun ScheduleContentWidget(
    state: ScheduleUiState,
    namedSchedule: NamedSchedule,
    scheduleVo: ScheduleVo,
    scheduleCalendarState: CalendarState?,
    scheduleListState: LazyListState,
    contentPadding: PaddingValues,
    onAction: (ScheduleAction) -> Unit
) {
    BackHandler(
        !namedSchedule.isDefault
    ) {
        onAction(ScheduleAction.OnReturnToDefaultClicked)
    }

    BackHandler(
        state.isRefreshing
    ) {
        onAction(ScheduleAction.OnCancelRefresh)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScheduleHeaderWidget(
                namedSchedule = namedSchedule,
                scheduleTypeName = scheduleVo.schedule.timetableType.typeName,
                scheduleView = state.scheduleView,
                onAction = onAction
            )
        }
    ) { padding ->
        if (scheduleCalendarState != null) {
            AppPullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                onRefresh = {
                    onAction(ScheduleAction.OnP2R)
                },
                enabled = state.isSaved,
                isRefreshing = state.isRefreshing
            ) {
                Column {
                    AnimatedAlert(
                        isHidden = state.isSaved
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(AppTheme.shapes.medium)
                                .background(
                                    AppTheme.colors.primary
                                )
                        ) {
                            AppCell(
                                contentPadding = PaddingValues(
                                    horizontal = AppTheme.paddings.padding12,
                                    vertical = AppTheme.paddings.padding4
                                ),
                                start = {
                                    Icon(
                                        icon = AppTheme.icons.info,
                                        iconSize = 24.dp,
                                        iconColor = AppTheme.colors.onPrimary
                                    )
                                },
                                center = {
                                    TextBlock(
                                        text = stringResource(R.string.schedule_is_not_saved),
                                        color = AppTheme.colors.onPrimary,
                                        typography = AppTheme.typography.titleSmall,
                                    )
                                },
                                end = {
                                    Custom {
                                        Button(
                                            colors = ButtonDefaults.buttonColors()
                                                .copy(
                                                    containerColor = AppTheme.colors.primary,
                                                    contentColor = AppTheme.colors.onPrimary
                                                ),
                                            onClick = {
                                                onAction(ScheduleAction.OnSaveScheduleClicked)
                                            },
                                            contentPadding = PaddingValues(
                                                horizontal = AppTheme.paddings.padding8,
                                                vertical = AppTheme.paddings.padding2
                                            )
                                        ) {
                                            CompositionLocalProvider(
                                                LocalMinimumInteractiveComponentSize provides 0.dp
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.schedule_save),
                                                    style = AppTheme.typography.titleSmall
                                                )
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    }
                    AnimatedContent(
                        targetState = state.scheduleView,
                        transitionSpec = {
                            fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 }
                            ) togetherWith fadeOut() +
                                    slideOutVertically(
                                        targetOffsetY = { it / 2 }
                                    )
                        }
                    ) { targetState ->
                        when (targetState) {
                            ScheduleView.LIST -> {
                                ScheduleListWidget(
                                    scheduleListState = scheduleListState,
                                    isSavedSchedule = state.isSaved,
                                    scheduleVo = scheduleVo,
                                    eventView = state.eventView,
                                    paddingBottom = contentPadding.calculateBottomPadding(),
                                    onAction = onAction
                                )
                            }

                            else -> {
                                ScheduleCalendarWidget(
                                    scheduleVo = scheduleVo,
                                    isSavedSchedule = state.isSaved,
                                    scheduleCalendarState = scheduleCalendarState,
                                    today = state.time.toLocalDate(),
                                    eventsCountView = state.eventsCountView,
                                    eventView = state.eventView,
                                    paddingBottom = contentPadding.calculateBottomPadding(),
                                    onAction = onAction
                                )
                            }
                        }
                    }
                }
            }
        } else {
            EmptyScreen(
                title = "¯\\_(ツ)_/¯",
                subtitle = stringResource(R.string.schedule_empty_here),
                isBoldTitle = false,
                paddingBottom = contentPadding.calculateBottomPadding()
            )
        }
    }

    if (state.scheduleSheet) {
        ScheduleSheetWidget(
            namedSchedule = namedSchedule,
            currentSchedule = scheduleVo.schedule,
            schedules = state.schedules,
            today = state.time.toLocalDate(),
            isSavedNamedSchedule = state.isSaved,
            haveHiddenEvents = scheduleVo.haveHiddenEvents,
            haveNotEmptySchedules = state.schedules.isNotEmpty(),
            onAction = onAction
        )
    }
}
