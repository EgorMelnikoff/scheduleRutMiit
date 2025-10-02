package com.egormelnikoff.schedulerutmiit.ui.screens.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.dialogs.DialogNamedScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateCurrentWeek
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeYellow
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.ScheduleUiState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReviewScreen(
    externalPadding: PaddingValues,

    navigateToAddSchedule: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,

    onSetNamedSchedule: (Long) -> Unit,
    onSelectDefaultNamedSchedule: (Long) -> Unit,
    onDeleteNamedSchedule: (Pair<Long, Boolean>) -> Unit,
    onShowEvent: (Long) -> Unit,

    onChangeSavedSchedulesVisibility: (Boolean) -> Unit,
    onChangeHiddenEventsVisibility: (Boolean) -> Unit,

    visibleSavedSchedules: Boolean,
    visibleHiddenEvents: Boolean,
    scheduleUiState: ScheduleUiState,
    today: LocalDate
) {
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText =  LocalContext.current.getString(R.string.review),
                actions = {
                    IconButton(
                        onClick = {
                            navigateToAddSchedule()
                        },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            navigateToSearch()
                        },
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (scheduleUiState.savedNamedSchedules.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 8.dp, end = 8.dp,
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = externalPadding.calculateBottomPadding()
                    )
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (
                    scheduleUiState.defaultScheduleData?.namedSchedule != null
                    && scheduleUiState.defaultScheduleData.settledScheduleEntity != null
                    && scheduleUiState.isSaved
                ) {
                    var eventsForTomorrow = listOf<Event>()
                    var countEventsForWeek = 0
                    scheduleUiState.defaultScheduleData.periodicEventsForCalendar?.let { periodicEvents ->
                        val recurrence =
                            scheduleUiState.defaultScheduleData.settledScheduleEntity.recurrence!!
                        val startDate =
                            scheduleUiState.defaultScheduleData.settledScheduleEntity.startDate


                        val currentWeek = calculateCurrentWeek(
                            date = today,
                            startDate = startDate,
                            interval = recurrence.interval!!,
                            firstPeriodNumber = recurrence.firstWeekNumber
                        )

                        val eventsForCurrentWeek = periodicEvents[currentWeek] ?: emptyMap()
                        val eventsForTomorrowPeriodic =
                            eventsForCurrentWeek[today.plusDays(1).dayOfWeek]

                        eventsForTomorrow = eventsForTomorrowPeriodic
                            ?.distinctBy { it.startDatetime }
                            ?: listOf()

                        countEventsForWeek = eventsForCurrentWeek
                            .flatMap { it.value }
                            .distinctBy { it.startDatetime }
                            .size

                    }
                    scheduleUiState.defaultScheduleData.nonPeriodicEventsForCalendar?.let { nonPeriodicEvents ->
                        val eventsForTomorrowNonPeriodic = nonPeriodicEvents[today.plusDays(1)]
                        eventsForTomorrow = eventsForTomorrowNonPeriodic ?: listOf()
                        countEventsForWeek = getEventsCountPerWeek(today, nonPeriodicEvents)
                    }
                    EventsReview(
                        navigateToEvent = navigateToEvent,
                        countEventsForWeek = countEventsForWeek,
                        eventsForTomorrow = eventsForTomorrow,
                        scheduleUiState = scheduleUiState
                    )
                }
                ExpandedItem(
                    title = LocalContext.current.getString(R.string.saved_schedules),
                    visible = visibleSavedSchedules,
                    onChangeVisibility = onChangeSavedSchedulesVisibility
                ) {
                    ColumnGroup(
                        items = scheduleUiState.savedNamedSchedules.map { namedScheduleEntity ->
                            {
                                NamedScheduleItem(
                                    namedScheduleEntity = namedScheduleEntity,
                                    onShowActionsDialog = {
                                        showNamedScheduleDialog = namedScheduleEntity
                                    }
                                )
                            }
                        }
                    )
                }
                if (!scheduleUiState.currentScheduleData?.hiddenEvents.isNullOrEmpty()) {
                    ExpandedItem(
                        title = LocalContext.current.getString(R.string.hidden_events),
                        visible = visibleHiddenEvents,
                        onChangeVisibility = onChangeHiddenEventsVisibility
                    ) {
                        ColumnGroup(
                            items = scheduleUiState.currentScheduleData!!.hiddenEvents.map {
                                {
                                    val eventExtraData =
                                        scheduleUiState.currentScheduleData.eventsExtraData.find { event -> it.id == event.id }

                                    HiddenEvent(
                                        event = it,
                                        onShowEvent = onShowEvent,
                                        navigateToEvent = {
                                            navigateToEvent(Pair(it, eventExtraData))
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        } else {
            ErrorScreen(
                button = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.search),
                            imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                            onClick = { navigateToSearch() },
                        )
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.create),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            onClick = { navigateToAddSchedule() },
                        )
                    }
                },

                paddingTop = innerPadding.calculateTopPadding(),
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        if (showNamedScheduleDialog != null) {
            DialogNamedScheduleActions(
                namedScheduleEntity = showNamedScheduleDialog!!,
                onSetSchedule = {
                    onSetNamedSchedule(showNamedScheduleDialog!!.id)
                },
                onSelectDefault = {
                    onSelectDefaultNamedSchedule(showNamedScheduleDialog!!.id)
                },
                onDelete = {
                    showDeleteNamedScheduleDialog = showNamedScheduleDialog!!
                },
                onDismiss = {
                    showNamedScheduleDialog = null
                }
            )
        }

        if (showDeleteNamedScheduleDialog != null) {
            CustomAlertDialog(
                dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                dialogTitle = "${LocalContext.current.getString(R.string.delete_schedule)}?",
                dialogText = LocalContext.current.getString(R.string.impossible_restore_eventextra),
                onDismissRequest = {
                    showDeleteNamedScheduleDialog = null
                },
                onConfirmation = {
                    onDeleteNamedSchedule(
                        Pair(
                            showDeleteNamedScheduleDialog!!.id,
                            showDeleteNamedScheduleDialog!!.isDefault
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun ExpandedItem(
    title: String,
    visible: Boolean,
    onChangeVisibility: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (visible) 180f else 0f,
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onChangeVisibility(!visible) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
                    .graphicsLayer(
                        rotationZ = rotationAngle
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.down),
                contentDescription = null
            )
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        AnimatedVisibility(
            modifier = Modifier.padding(horizontal = 8.dp),
            visible = visible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            content.invoke()
        }
    }
}

@Composable
fun EventsReview(
    eventsForTomorrow: List<Event>,
    countEventsForWeek: Int,
    scheduleUiState: ScheduleUiState,
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RowGroup(
            items = listOf(
                {
                    EventsCount(
                        title = LocalContext.current.getString(R.string.tomorrow),
                        value = eventsForTomorrow.size.toString(),
                        comment = LocalResources.current.getQuantityString(
                            R.plurals.events,
                            eventsForTomorrow.size
                        )
                    )
                }, {
                    EventsCount(
                        title = LocalContext.current.getString(R.string.week).replaceFirstChar { it.uppercase() },
                        value = countEventsForWeek.toString(),
                        comment = LocalResources.current.getQuantityString(
                            R.plurals.events,
                            countEventsForWeek
                        )
                    )
                }
            )
        )
        val haveAnyEventsExtraData = eventsForTomorrow.any {
            scheduleUiState.defaultScheduleData!!.eventsExtraData.find { eventExtraData ->
                it.id == eventExtraData.id && eventExtraData.comment != ""
            } != null
        }

        if (eventsForTomorrow.isNotEmpty() && haveAnyEventsExtraData) {
            ColumnGroup(
                title = LocalContext.current.getString(R.string.comments_on_tomorrow_events),
                items = eventsForTomorrow.mapNotNull { event ->
                    val eventExtraData =
                        scheduleUiState.defaultScheduleData!!.eventsExtraData.find { it.id == event.id }

                    if (eventExtraData != null && eventExtraData.comment != "") {
                        {
                            EventExtra(
                                event = event,
                                eventExtraData = eventExtraData,
                                onCLick = {
                                    navigateToEvent(Pair(event, eventExtraData))
                                }
                            )
                        }
                    } else {
                        null
                    }
                }
            )
        }
    }
}

@Composable
fun EventsCount(
    title: String,
    value: String,
    comment: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            modifier = Modifier,
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = value,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = comment,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EventExtra(
    event: Event,
    eventExtraData: EventExtraData,
    onCLick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCLick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = event.name!!,
            fontSize = 16.sp,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (eventExtraData.comment != "") {
            val color = when (eventExtraData.tag) {
                1 -> darkThemeRed
                2 -> darkThemeOrange
                3 -> darkThemeYellow
                4 -> darkThemeGreen
                5 -> darkThemeLightBlue
                6 -> darkThemeBlue
                7 -> darkThemeViolet
                8 -> darkThemePink
                else -> Color.Unspecified
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (eventExtraData.tag != 0) {
                    Canvas(
                        modifier = Modifier.size(8.dp)
                    ) {
                        drawCircle(
                            color = color,
                            center = center
                        )
                    }
                }
                Text(
                    text = eventExtraData.comment,
                    fontSize = 12.sp,
                    maxLines = 1,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun NamedScheduleItem(
    onShowActionsDialog: () -> Unit,
    namedScheduleEntity: NamedScheduleEntity
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onShowActionsDialog()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .defaultMinSize(minHeight = 52.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val instant = Instant.ofEpochMilli(namedScheduleEntity.lastTimeUpdate)
            val formatter = DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.getDefault())
            val lastTimeUpdate =
                formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()))
            Text(
                text = namedScheduleEntity.shortName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (namedScheduleEntity.type != 3) {
                Text(
                    text = "${LocalContext.current.getString(R.string.Current_on)} $lastTimeUpdate",
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1, style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (namedScheduleEntity.isDefault) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HiddenEvent(
    event: Event,
    navigateToEvent: () -> Unit,
    onShowEvent: (Long) -> Unit,
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:MM")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToEvent() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = event.name!!,
                fontSize = 16.sp,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (event.recurrenceRule != null) {
                        val day = event.startDatetime!!.dayOfWeek.getDisplayName(
                            java.time.format.TextStyle.FULL,
                            Locale.getDefault()
                        ).replaceFirstChar { it.uppercase() }
                        val startTime = event.startDatetime.atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime().format(timeFormatter)
                        val endTime = event.endDatetime!!.atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime().format(timeFormatter)
                        "$day, $startTime - $endTime"
                    } else {
                        event.startDatetime!!.format(dateTimeFormatter)
                    },
                    fontSize = 12.sp,
                    maxLines = 1,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }
        IconButton(
            onClick = {
                onShowEvent(event.id)
            },
            colors = IconButtonDefaults.iconButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.visibility),
                contentDescription = null
            )
        }
    }
}

fun getEventsCountPerWeek(
    today: LocalDate,
    events: Map<LocalDate, List<Event>>
): Int {
    var count = 0
    val firstDayOfWeek = calculateFirstDayOfWeek(today)
    for (date in 0 until 7) {
        val currentDate = firstDayOfWeek.plusDays(date.toLong())
        val eventPerDay = events[currentDate]?.distinctBy { it.startDatetime }
        count += eventPerDay?.size ?: 0
    }
    return count
}