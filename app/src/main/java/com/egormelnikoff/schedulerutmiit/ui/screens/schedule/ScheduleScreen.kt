package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.ScheduleTopBar
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Composable
fun ScreenSchedule(
    paddingValues: PaddingValues,
    navigateToSearch: () -> Unit,
    navigateToAddEvent: (ScheduleEntity) -> Unit,
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    expandedSchedulesMenu: Boolean,
    onShowExpandedMenu: (Boolean) -> Unit,
    scheduleViewModel: ScheduleViewModel,
    preferencesDataStore: DataStore,
    appSettings: AppSettings,

    scheduleUiState: ScheduleUiState,
    scheduleCalendarParams: ScheduleCalendarParams,
    scheduleListState: LazyListState,
    today: LocalDate
) {
    when {
        scheduleUiState.isLoading -> {
            LoadingScreen(
                paddingTop = paddingValues.calculateTopPadding(),
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }

        scheduleUiState.isError -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.error),
                subtitle = LocalContext.current.getString(R.string.error_load_schedule),
                paddingTop = paddingValues.calculateTopPadding(),
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }

        scheduleUiState.currentNamedSchedule != null -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    val showLoadDefaultSchedule = scheduleUiState.savedNamedSchedules.isNotEmpty()
                            && !scheduleUiState.currentNamedSchedule.namedScheduleEntity.isDefault
                    ScheduleTopBar(
                        onShowExpandedMenu = onShowExpandedMenu,

                        onLoadDefaultSchedule = {
                            scheduleViewModel.loadInitialData(false)
                        },

                        navigateToAddEvent = navigateToAddEvent,

                        scheduleViewModel = scheduleViewModel,
                        preferencesDataStore = preferencesDataStore,

                        scheduleUiState = scheduleUiState,
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        onShowLoadDefaultScheduleButton = showLoadDefaultSchedule,
                        isScheduleCalendar = appSettings.calendarView,
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding())
                ) {
                    AnimatedVisibility(
                        visible = scheduleUiState.isUpdating,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                    ExpandedMenu(
                        scheduleViewModel = scheduleViewModel,
                        scheduleUiState = scheduleUiState,
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        onShowExpandedMenu = onShowExpandedMenu
                    )
                    if (scheduleUiState.currentScheduleData != null && scheduleUiState.currentScheduleEntity != null) {
                        AnimatedContent(
                            targetState = appSettings.calendarView,
                            transitionSpec = {
                                fadeIn() + slideInVertically(
                                    initialOffsetY = {
                                        it / 2
                                    }
                                ) togetherWith fadeOut() +
                                        slideOutVertically(
                                            targetOffsetY = {
                                                it / 2
                                            }
                                        )
                            }
                        ) { targetState ->
                            if (targetState) {
                                ScheduleCalendarView(
                                    navigateToEvent = navigateToEvent,
                                    onDeleteEvent = { primaryKey ->
                                        scheduleViewModel.deleteCustomEvent(scheduleUiState.currentScheduleEntity, primaryKey)
                                    },
                                    isSavedSchedule = scheduleUiState.isSaved,
                                    isShowCountClasses = appSettings.showCountClasses,
                                    isShortEvent = appSettings.eventView,

                                    scheduleUiState = scheduleUiState,
                                    today = today,
                                    scheduleCalendarParams = scheduleCalendarParams,
                                    paddingBottom = paddingValues.calculateBottomPadding()
                                )
                            } else {
                                ScheduleListView(
                                    navigateToEvent = navigateToEvent,
                                    onDeleteEvent = { primaryKey ->
                                        scheduleViewModel.deleteCustomEvent(scheduleUiState.currentScheduleEntity, primaryKey)
                                    },
                                    isSavedSchedule = scheduleUiState.isSaved,
                                    scheduleEntity = scheduleUiState.currentScheduleEntity,
                                    eventsForList = scheduleUiState.currentScheduleData.eventForList,
                                    eventsExtraData = scheduleUiState.currentScheduleData.eventsExtraData,
                                    scheduleListState = scheduleListState,
                                    isShortEvent = appSettings.eventView,
                                    paddingBottom = paddingValues.calculateBottomPadding(),
                                )
                            }
                        }
                    } else {
                        Empty(
                            title = "¯\\_(ツ)_/¯",
                            subtitle = LocalContext.current.getString(R.string.empty_here),
                            isBoldTitle = false,
                            paddingBottom = paddingValues.calculateBottomPadding()
                        )
                    }
                }
            }
        }


        scheduleUiState.savedNamedSchedules.isEmpty() -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                buttonTitle = LocalContext.current.getString(R.string.search),
                imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                action = { navigateToSearch() },
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }
    }
}

@Composable
fun ExpandedMenu(
    scheduleViewModel: ScheduleViewModel,
    scheduleUiState: ScheduleUiState,
    expandedSchedulesMenu: Boolean,
    onShowExpandedMenu: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = expandedSchedulesMenu,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
            scheduleUiState.currentNamedSchedule!!.schedules.forEach { schedule ->
                val scale by animateFloatAsState(
                    targetValue = if (schedule.scheduleEntity.isDefault) 1f else 0f
                )
                ExpandedMenuItem(
                    onClick = {
                        scheduleViewModel.setNewDefaultSchedule(
                            primaryKeySchedule = schedule.scheduleEntity.id,
                            timetableId = schedule.scheduleEntity.timetableId
                        )
                    },
                    title = {
                        Column {
                            Text(
                                text = schedule.scheduleEntity.typeName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = schedule.scheduleEntity.startDate.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ),
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    modifier = Modifier.size(12.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.forward),
                                    contentDescription = null
                                )
                                Text(
                                    text = schedule.scheduleEntity.endDate.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ),
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    scale = scale,
                    trailingIcon = ImageVector.vectorResource(R.drawable.check),
                    verticalPadding = 4,
                    trailingIconColor = MaterialTheme.colorScheme.primary
                )
            }
            ExpandedMenuItem(
                onClick = {
                    onShowExpandedMenu(false)
                },
                title = {
                    Text(
                        text = LocalContext.current.getString(R.string.collapse),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = ImageVector.vectorResource(R.drawable.up),
                verticalPadding = 4
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun ExpandedMenuItem(
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    title: @Composable (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    scale: Float? = null,
    verticalPadding: Int,
    trailingIconColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = verticalPadding.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            title?.invoke()
        }
        if (trailingIcon != null) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .let {
                        if (scale != null)
                            it.graphicsLayer(scaleX = scale, scaleY = scale)
                        else it
                    },
                imageVector = trailingIcon,
                contentDescription = null,
                tint = trailingIconColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun calculateFirstDayOfWeek(date: LocalDate): LocalDate {
    return date.minusDays(date.dayOfWeek.value - 1L)
}

fun calculateCurrentWeek(
    date: LocalDate,
    startDate: LocalDate,
    firstPeriodNumber: Int,
    interval: Int
): Int {
    val weeksFromStart = abs(ChronoUnit.WEEKS.between(date, startDate)).plus(1).toInt()
    return ((weeksFromStart + firstPeriodNumber) % interval).plus(1)
}