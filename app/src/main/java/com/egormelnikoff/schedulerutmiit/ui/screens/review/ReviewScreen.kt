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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.elements.ExpandedItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigateEventDialog
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.theme.getColorByIndex
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReviewScreen(
    scheduleState: ScheduleState,
    reviewUiState: ReviewUiState,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions,
    externalPadding: PaddingValues
) {
    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }

    Scaffold(
        topBar = {
            if (scheduleState.savedNamedSchedules.isNotEmpty()) {
                CustomTopAppBar(
                    titleText = LocalContext.current.getString(R.string.review),
                    actions = {
                        IconButton(
                            onClick = {
                                navigationActions.navigateToAddSchedule()
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
                                navigationActions.navigateToSearch()
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
        if (scheduleState.savedNamedSchedules.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = externalPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (
                    scheduleState.defaultNamedScheduleData?.scheduleData?.reviewData != null &&
                    scheduleState.defaultNamedScheduleData.namedSchedule != null &&
                    scheduleState.defaultNamedScheduleData.scheduleData.scheduleEntity != null
                ) {
                    item {
                        EventsReview(
                            navigateToEvent = navigationActions.navigateToEvent,
                            scheduleState = scheduleState,
                            title = "${scheduleState.defaultNamedScheduleData.namedSchedule.namedScheduleEntity.shortName} " +
                                    "(${scheduleState.defaultNamedScheduleData.scheduleData.scheduleEntity.typeName})"
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
                item {
                    ExpandedItem(
                        title = LocalContext.current.getString(R.string.saved_schedules),
                        imageVector = ImageVector.vectorResource(R.drawable.save),
                        visible = reviewUiState.visibleSavedSchedules,
                        onChangeVisibility = reviewUiState.onChangeVisibilitySavedSchedules
                    ) {
                        ColumnGroup(
                            items = scheduleState.savedNamedSchedules.map { namedScheduleEntity ->
                                {
                                    val formatter =
                                        DateTimeFormatter.ofPattern(
                                            "d MMMM",
                                            Locale.getDefault()
                                        )
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
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = LocalContext.current.getString(R.string.find),
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            onClick = { navigationActions.navigateToSearch() },
                        )
                        CustomButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = LocalContext.current.getString(R.string.create),
                            imageVector = ImageVector.vectorResource(R.drawable.add),
                            onClick = { navigationActions.navigateToAddSchedule() },
                        )
                    }
                },
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        if (showNamedScheduleDialog != null) {
            ModalDialogNamedSchedule(
                namedScheduleEntity = showNamedScheduleDialog!!,
                navigateToRenameDialog = {
                    navigationActions.navigateToRenameDialog(showNamedScheduleDialog!!)
                },
                onDismiss = {
                    showNamedScheduleDialog = null
                },
                onOpenNamedSchedule = {
                    scheduleActions.onOpenNamedSchedule(showNamedScheduleDialog!!.id)
                    navigationActions.navigateToSchedule()
                },
                onSetDefaultNamedSchedule = if (!showNamedScheduleDialog!!.isDefault) {
                    { scheduleActions.onSelectDefaultNamedSchedule(showNamedScheduleDialog!!.id) }
                } else null,
                onDeleteNamedSchedule = {
                    showDeleteNamedScheduleDialog = showNamedScheduleDialog!!
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
                    scheduleActions.onDeleteNamedSchedule(
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
    navigateToEvent: (NavigateEventDialog) -> Unit,
    title: String,
    scheduleState: ScheduleState
) {
    val scheduleEntity = scheduleState.defaultNamedScheduleData!!.scheduleData!!.scheduleEntity!!
    val reviewData = scheduleState.defaultNamedScheduleData.scheduleData.reviewData!!
    val eventsExtraData = scheduleState.defaultNamedScheduleData.scheduleData.eventsExtraData
    val today = LocalDate.now()
    Column(
        modifier = Modifier
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RowGroup(
            title = title,
            items = listOf(
                {
                    EventsCount(
                        title = when (reviewData.displayedDate) {
                            today -> {
                                LocalContext.current.getString(R.string.today)
                            }

                            today.plusDays(1) -> {
                                LocalContext.current.getString(R.string.tomorrow)
                            }

                            else -> {
                                reviewData.displayedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            }
                        },
                        value = reviewData.events.size.toString(),
                        comment = LocalResources.current.getQuantityString(
                            R.plurals.events,
                            reviewData.events.size
                        )
                    )
                }, {
                    EventsCount(
                        title = when {
                            reviewData.displayedDate == today.plusDays(1)
                                    && reviewData.displayedDate.dayOfWeek == DayOfWeek.MONDAY -> {
                                LocalContext.current.getString(R.string.next_week)
                            }

                            else -> {
                                LocalContext.current.getString(R.string.week, "")
                            }
                        },
                        value = reviewData.countEventsForWeek.toString(),
                        comment = LocalResources.current.getQuantityString(
                            R.plurals.events,
                            reviewData.countEventsForWeek
                        )
                    )
                }
            )
        )
        val haveAnyEventsExtraData = reviewData.events.values.flatten().any {
            eventsExtraData.find { eventExtraData ->
                it.id == eventExtraData.id && eventExtraData.comment != ""
            } != null
        }

        if (reviewData.events.isNotEmpty() && haveAnyEventsExtraData) {
            ColumnGroup(
                title = if (reviewData.displayedDate == today.plusDays(1)) {
                    LocalContext.current.getString(R.string.comments_on_tomorrow_events)
                } else LocalContext.current.getString(R.string.comments_on_events),
                items = reviewData.events.values.flatten().mapNotNull { event ->
                    val eventExtraData = eventsExtraData
                        .find { it.id == event.id }

                    if (eventExtraData != null && eventExtraData.comment != "") {
                        {
                            ClickableItem(
                                title = event.name!!,
                                subtitle = eventExtraData.comment,
                                showClickLabel = false,
                                subtitleLabel = if (eventExtraData.tag != 0) {
                                    {
                                        Icon(
                                            modifier = Modifier.size(8.dp),
                                            imageVector = ImageVector.vectorResource(R.drawable.circle),
                                            contentDescription = null,
                                            tint = getColorByIndex(eventExtraData.tag)
                                        )
                                    }
                                } else null,
                                onClick = {
                                    navigateToEvent(
                                        NavigateEventDialog(
                                            scheduleEntity = scheduleEntity,
                                            isSavedSchedule = true,
                                            isCustomSchedule = scheduleState.defaultNamedScheduleData.namedSchedule?.namedScheduleEntity?.type == 3,
                                            event = event,
                                            eventExtraData = eventExtraData
                                        )
                                    )
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