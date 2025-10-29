package com.egormelnikoff.schedulerutmiit.ui.screens.review

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.ui.dialogs.DialogNamedScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.ExpandedItem
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.theme.Blue
import com.egormelnikoff.schedulerutmiit.ui.theme.Green
import com.egormelnikoff.schedulerutmiit.ui.theme.LightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.Orange
import com.egormelnikoff.schedulerutmiit.ui.theme.Pink
import com.egormelnikoff.schedulerutmiit.ui.theme.Red
import com.egormelnikoff.schedulerutmiit.ui.theme.Violet
import com.egormelnikoff.schedulerutmiit.ui.theme.Yellow
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleUiState
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReviewScreen(
    externalPadding: PaddingValues,
    today: LocalDate,
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
    scheduleUiState: ScheduleUiState
) {
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, hh:MM")

    Scaffold(
        topBar = {
            if (scheduleUiState.savedNamedSchedules.isNotEmpty()) {
                CustomTopAppBar(
                    titleText = LocalContext.current.getString(R.string.review),
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
                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (scheduleUiState.savedNamedSchedules.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 16.dp, end = 16.dp,
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = externalPadding.calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (
                    scheduleUiState.defaultScheduleData?.namedSchedule != null
                    && scheduleUiState.defaultScheduleData.settledScheduleEntity != null
                ) {
                    EventsReview(
                        displayedDate = today,
                        navigateToEvent = navigateToEvent,
                        scheduleUiState = scheduleUiState
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                ExpandedItem(
                    title = LocalContext.current.getString(R.string.saved_schedules),
                    imageVector = ImageVector.vectorResource(R.drawable.save),
                    visible = visibleSavedSchedules,
                    onChangeVisibility = onChangeSavedSchedulesVisibility
                ) {
                    ColumnGroup(
                        items = scheduleUiState.savedNamedSchedules.map { namedScheduleEntity ->
                            {
                                val formatter =
                                    DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
                                val lastTimeUpdate =
                                    formatter.format(
                                        LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(namedScheduleEntity.lastTimeUpdate),
                                            ZoneId.systemDefault()
                                        )
                                    )

                                ClickableItem(
                                    title = namedScheduleEntity.shortName,
                                    titleMaxLines = 2,
                                    subtitle = if (namedScheduleEntity.type != 3) {
                                        "${LocalContext.current.getString(R.string.current_on)} $lastTimeUpdate"
                                    } else null,
                                    defaultMinHeight = 40.dp,
                                    onClick = {
                                        showNamedScheduleDialog = namedScheduleEntity
                                    },
                                    trailingIcon = if (namedScheduleEntity.isDefault) {
                                        {
                                            Icon(
                                                modifier = Modifier.size(20.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.check),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    )
                }
                if (!scheduleUiState.currentScheduleData?.hiddenEvents.isNullOrEmpty()) {
                    ExpandedItem(
                        title = LocalContext.current.getString(R.string.hidden_events),
                        imageVector = ImageVector.vectorResource(R.drawable.visibility_off),
                        visible = visibleHiddenEvents,
                        onChangeVisibility = onChangeHiddenEventsVisibility
                    ) {
                        ColumnGroup(
                            items = scheduleUiState.currentScheduleData.hiddenEvents.map {
                                {
                                    val eventExtraData =
                                        scheduleUiState.currentScheduleData.eventsExtraData.find { event -> it.id == event.id }
                                    ClickableItem(
                                        title = it.name!!,
                                        titleMaxLines = 2,
                                        subtitle = if (it.recurrenceRule != null) {
                                            val day = it.startDatetime!!.dayOfWeek.getDisplayName(
                                                java.time.format.TextStyle.FULL,
                                                Locale.getDefault()
                                            ).replaceFirstChar { c -> c.uppercase() }
                                            val startTime =
                                                it.startDatetime.toLocaleTimeWithTimeZone()
                                                    .format(timeFormatter)
                                            val endTime =
                                                it.endDatetime!!.toLocaleTimeWithTimeZone()
                                                    .format(timeFormatter)
                                            "$day, $startTime - $endTime"
                                        } else {
                                            it.startDatetime!!.format(dateTimeFormatter)
                                        },
                                        trailingIcon = {
                                            IconButton(
                                                onClick = {
                                                    onShowEvent(it.id)
                                                },
                                                colors = IconButtonDefaults.iconButtonColors().copy(
                                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .size(24.dp),
                                                    imageVector = ImageVector.vectorResource(R.drawable.visibility),
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        showClickLabel = false,
                                        onClick = {
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
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                button = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.find),
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            onClick = { navigateToSearch() },
                        )
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.create),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            onClick = { navigateToAddSchedule() },
                        )
                    }
                },
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
                },
                isSavedNamedSchedule = true
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
fun EventsReview(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    displayedDate: LocalDate,
    scheduleUiState: ScheduleUiState
) {
    Column(
        modifier = Modifier
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RowGroup(
            items = listOf(
                {
                    EventsCount(
                        title = LocalContext.current.getString(R.string.tomorrow),
                        value = scheduleUiState.defaultScheduleData!!.eventsForTomorrow.size.toString(),
                        comment = LocalResources.current.getQuantityString(
                            R.plurals.events,
                            scheduleUiState.defaultScheduleData.eventsForTomorrow.size
                        )
                    )
                }, {
                    EventsCount(
                        title = if (displayedDate.dayOfWeek != DayOfWeek.SUNDAY) {
                            LocalContext.current.getString(R.string.week)
                        } else {
                            LocalContext.current.getString(R.string.next_week)
                        },
                        value = scheduleUiState.defaultScheduleData!!.countEventsForWeek.toString(),
                        comment = LocalResources.current.getQuantityString(
                            R.plurals.events,
                            scheduleUiState.defaultScheduleData.countEventsForWeek
                        )
                    )
                }
            )
        )
        val haveAnyEventsExtraData = scheduleUiState.defaultScheduleData!!.eventsForTomorrow.any {
            scheduleUiState.defaultScheduleData.eventsExtraData.find { eventExtraData ->
                it.id == eventExtraData.id && eventExtraData.comment != ""
            } != null
        }

        if (scheduleUiState.defaultScheduleData.eventsForTomorrow.isNotEmpty() && haveAnyEventsExtraData) {
            ColumnGroup(
                title = LocalContext.current.getString(R.string.comments_on_tomorrow_events),
                items = scheduleUiState.defaultScheduleData.eventsForTomorrow.mapNotNull { event ->
                    val eventExtraData = scheduleUiState.defaultScheduleData.eventsExtraData
                        .find { it.id == event.id }

                    if (eventExtraData != null && eventExtraData.comment != "") {
                        {
                            ClickableItem(
                                title = event.name!!,
                                subtitle = eventExtraData.comment,
                                showClickLabel = false,
                                subtitleLabel = if (eventExtraData.tag != 0) {
                                    val color = when (eventExtraData.tag) {
                                        1 -> Red
                                        2 -> Orange
                                        3 -> Yellow
                                        4 -> Green
                                        5 -> LightBlue
                                        6 -> Blue
                                        7 -> Violet
                                        8 -> Pink
                                        else -> Color.Unspecified
                                    }
                                    {
                                        Icon(
                                            modifier = Modifier.size(8.dp),
                                            imageVector = ImageVector.vectorResource(R.drawable.circle),
                                            contentDescription = null,
                                            tint = color
                                        )
                                    }
                                } else null,
                                onClick = {
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier,
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = comment,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}