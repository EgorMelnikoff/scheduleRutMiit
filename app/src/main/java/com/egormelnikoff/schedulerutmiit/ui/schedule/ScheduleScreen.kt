package com.egormelnikoff.schedulerutmiit.ui.schedule

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.DataStore
import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Composable
fun ScreenSchedule(
    navigateToSearch: () -> Unit,
    onShowDialogEvent: (Boolean) -> Unit,
    onSelectDisplayedEvent: (Event) -> Unit,

    showDialogEvent: Boolean,
    displayedEvent: Event?,

    scheduleViewModel: ScheduleViewModel,
    preferencesDataStore: DataStore,
    appSettings: AppSettings,

    scheduleState: ScheduleState,

    snackbarHostState: SnackbarHostState,
    schedulesData: MutableMap<String, ScheduleData>,
    scheduleListState: LazyListState,
    today: LocalDate,
    paddingValues: PaddingValues
) {
    var expandedSchedulesMenu by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if ((scheduleState !is ScheduleState.Loaded || !scheduleState.namedSchedule.namedScheduleEntity.isDefault) && scheduleState !is ScheduleState.EmptyBase) {
        BackHandler {
            scope.launch {
                scheduleViewModel.load()
            }
        }
    }

    when (scheduleState) {
        is ScheduleState.Loaded -> {
            LaunchedEffect(scheduleState.message) {
                if (scheduleState.message != null) {
                    snackbarHostState.showSnackbar(
                        message = scheduleState.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
            if (!scheduleState.isSaved || !scheduleState.namedSchedule.namedScheduleEntity.isDefault) {
                BackHandler {
                    scope.launch {
                        scheduleViewModel.load()
                    }
                }
            }
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                targetState = showDialogEvent,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { targetState ->
                if (targetState) {
                    EventDialog(
                        scheduleViewModel = scheduleViewModel,
                        scheduleId = scheduleState.selectedSchedule!!.scheduleEntity.id,
                        namedScheduleId = scheduleState.namedSchedule.namedScheduleEntity.id,
                        isSavedSchedule = scheduleState.isSaved,
                        event = displayedEvent!!,
                        eventExtraData = scheduleState.selectedSchedule.eventsExtraData.find { it.id == displayedEvent.id },
                        onShowEventDialog = onShowDialogEvent
                    )
                    BackHandler {
                        onShowDialogEvent(false)
                    }
                } else {
                    Column {
                        TopBar(
                            onShowExpandedMenu = { newValue ->
                                expandedSchedulesMenu = newValue
                            },
                            scheduleViewModel = scheduleViewModel,
                            preferencesDataStore = preferencesDataStore,

                            scheduleState = scheduleState,

                            expandedSchedulesMenu = expandedSchedulesMenu,
                            isScheduleCalendar = appSettings.calendarView
                        )
                        if (schedulesData.isNotEmpty() && !scheduleState.selectedSchedule?.events.isNullOrEmpty()) {
                            val eventsByWeekAndDays =
                                remember(scheduleState.selectedSchedule!!.scheduleEntity, scheduleState.namedSchedule.namedScheduleEntity.id) {
                                    calculateEventsByWeeks(scheduleState)
                                }
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
                                        onShowDialogEvent = onShowDialogEvent,
                                        onSelectDisplayedEvent = onSelectDisplayedEvent,

                                        isShowCountClasses = appSettings.showCountClasses,
                                        isShortEvent = appSettings.eventView,

                                        scheduleEntity = scheduleState.selectedSchedule.scheduleEntity,
                                        scheduleData = schedulesData[scheduleState.selectedSchedule.scheduleEntity.timetableId]!!,
                                        eventsByWeekAndDays = eventsByWeekAndDays,
                                        eventsExtraData = scheduleState.selectedSchedule.eventsExtraData,

                                        startDate = scheduleState.selectedSchedule.scheduleEntity.startDate,
                                        today = today,
                                        paddingBottom = paddingValues.calculateBottomPadding()
                                    )
                                } else {
                                    ScheduleListView(
                                        onShowDialogEvent = onShowDialogEvent,
                                        onSelectDisplayedEvent = onSelectDisplayedEvent,
                                        scheduleEntity = scheduleState.selectedSchedule.scheduleEntity,
                                        isShortEvent = appSettings.eventView,
                                        eventsByWeekAndDays = eventsByWeekAndDays,
                                        eventsExtraData = scheduleState.selectedSchedule.eventsExtraData,
                                        scheduleListState = scheduleListState,
                                        today = today,
                                        paddingBottom = paddingValues.calculateBottomPadding()
                                    )
                                }
                            }
                        } else {
                            Empty(
                                title = "¯\\_(ツ)_/¯",
                                subtitle = LocalContext.current.getString(R.string.empty_here),
                                isBoldTitle = false
                            )
                        }
                    }
                }
            }
        }

        is ScheduleState.EmptyBase -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                buttonTitle = LocalContext.current.getString(R.string.search),
                imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                action = { navigateToSearch() }
            )
        }

        is ScheduleState.Loading -> {
            LoadingScreen(
                paddingTop = paddingValues.calculateTopPadding(),
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }

        is ScheduleState.Error -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.error),
                subtitle = LocalContext.current.getString(R.string.error_load_schedule),
            )
        }
    }
}

@Composable
fun TopBar(
    scheduleState: ScheduleState.Loaded,
    scheduleViewModel: ScheduleViewModel,

    onShowExpandedMenu: (Boolean) -> Unit,
    expandedSchedulesMenu: Boolean,

    preferencesDataStore: DataStore,
    isScheduleCalendar: Boolean
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val rotationAngle by animateFloatAsState(
        targetValue = if (expandedSchedulesMenu) 180f else 0f
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .let {
                            if (scheduleState.namedSchedule.schedules.size > 1) {
                                it
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = {
                                        onShowExpandedMenu(!expandedSchedulesMenu)
                                    })
                            } else {
                                it
                            }
                        }
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column {
                        Text(
                            text = scheduleState.namedSchedule.namedScheduleEntity.shortName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (scheduleState.namedSchedule.schedules.isNotEmpty()) {
                            Text(
                                text = scheduleState.selectedSchedule!!.scheduleEntity.typeName,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                    }
                    if (scheduleState.namedSchedule.schedules.size > 1) {
                        Icon(
                            modifier = Modifier.graphicsLayer(
                                rotationZ = rotationAngle
                            ),
                            imageVector = ImageVector.vectorResource(R.drawable.down),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                }

            }
            if (scheduleState.namedSchedule.schedules.isNotEmpty() && scheduleState.selectedSchedule?.events?.isNotEmpty() == true) {
                IconButton(
                    onClick = {
                        val url = scheduleState.selectedSchedule.scheduleEntity.downloadUrl
                        val intent = Intent(Intent.ACTION_VIEW, url?.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.download),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            if (isScheduleCalendar) {
                                preferencesDataStore.setScheduleView(false)
                            } else {
                                preferencesDataStore.setScheduleView(true)
                            }
                        }

                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = if (isScheduleCalendar) ImageVector.vectorResource(R.drawable.list) else ImageVector.vectorResource(
                            R.drawable.calendar
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            IconToggleButton(
                checked = scheduleState.isSaved,
                enabled = if (scheduleState.isSaved) true else scheduleState.isSavingAvailable,
                colors = IconToggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    checkedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = Color.Unspecified,

                    contentColor = MaterialTheme.colorScheme.onBackground,
                    checkedContentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ),
                onCheckedChange = {
                    if (scheduleState.isSaved) {
                        scheduleViewModel.deleteNamedSchedule(
                            scheduleState.namedSchedule.namedScheduleEntity.id,
                            scheduleState.namedSchedule.namedScheduleEntity.isDefault
                        )
                    } else {
                        scheduleViewModel.saveSchedule()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = if (scheduleState.isSaved) ImageVector.vectorResource(R.drawable.save_fill) else ImageVector.vectorResource(
                        R.drawable.save
                    ),
                    contentDescription = null
                )
            }
        }
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
                scheduleState.namedSchedule.schedules.forEach { schedule ->
                    val scale by animateFloatAsState(
                        targetValue = if (schedule.scheduleEntity.isDefault) 1f else 0f
                    )
                    ExpandedMenuItem(
                        onClick = {
                            scheduleViewModel.selectSchedule(
                                primaryKeySchedule = schedule.scheduleEntity.id,
                                primaryKeyNamedSchedule = scheduleState.namedSchedule.namedScheduleEntity.id,
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


fun calculateDefaultDate(
    today: LocalDate,
    weeksCount: Int,
    scheduleEntity: ScheduleEntity
): Triple<LocalDate, Int, Int> {

    val defaultDate: LocalDate
    val weeksStartIndex: Int
    val daysStartIndex: Int

    if (today in scheduleEntity.startDate..scheduleEntity.endDate) {
        weeksStartIndex = abs(
            ChronoUnit.WEEKS.between(
                scheduleEntity.startDate,
                today
            ).toInt()
        )
        daysStartIndex = abs(
            ChronoUnit.DAYS.between(
                scheduleEntity.startDate,
                today
            ).toInt()
        )
        defaultDate = today
    } else if (today < scheduleEntity.startDate) {
        weeksStartIndex = 0
        daysStartIndex = 0
        defaultDate = scheduleEntity.startDate
    } else {
        weeksStartIndex = weeksCount
        daysStartIndex = weeksCount * 7
        defaultDate = scheduleEntity.endDate
    }
    return Triple(defaultDate, weeksStartIndex, daysStartIndex)
}


fun calculateEventsByWeeks(
    scheduleState: ScheduleState.Loaded
): MutableMap<Int, Map<LocalDate, List<Event>>> {
    val eventsByWeekAndDay = mutableMapOf<Int, Map<LocalDate, List<Event>>>()
    if (scheduleState.selectedSchedule!!.scheduleEntity.recurrence != null) {
        for (week in 1..scheduleState.selectedSchedule.scheduleEntity.recurrence!!.interval!!) {
            val eventsForWeek =
                scheduleState.selectedSchedule.events.filter { event ->
                    event.recurrenceRule?.let { rule ->
                        rule.interval == 1 || (rule.interval > 1 && week == event.periodNumber)
                    } ?: false
                }


            eventsByWeekAndDay[week] =
                eventsForWeek.groupBy { it.startDatetime!!.toLocalDate() }

        }
    } else {
        eventsByWeekAndDay[1] =
            scheduleState.selectedSchedule.events.groupBy { it.startDatetime!!.toLocalDate() }

    }
    return eventsByWeekAndDay
}