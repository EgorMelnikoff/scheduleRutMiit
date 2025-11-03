package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.toLocaleTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.theme.getColorByIndex

@Composable
fun ScheduleEvent(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,
    events: List<Event>,
    eventsExtraData: List<EventExtraData>,
    isSavedSchedule: Boolean,
    isShortEvent: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (events.first().timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = events.first().timeSlotName.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text =
                    "${
                        events.first().startDatetime!!.toLocaleTimeWithTimeZone()
                    } - ${
                        events.first().endDatetime!!.toLocaleTimeWithTimeZone()
                    }",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        ColumnGroup(
            items = events.map { event ->
                {
                    ScheduleSingleEvent(
                        navigateToEvent = navigateToEvent,
                        onDeleteEvent = onDeleteEvent,
                        onUpdateHiddenEvent = onUpdateHiddenEvent,
                        event = event,
                        eventExtraData = eventsExtraData.find {
                            it.id == event.id
                        },
                        isSavedSchedule = isSavedSchedule,
                        isShortEvent = isShortEvent
                    )
                }
            }
        )
    }
}

@Composable
fun ScheduleSingleEvent(
    navigateToEvent: (Pair<Event, EventExtraData?>) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,
    isSavedSchedule: Boolean,
    isShortEvent: Boolean,
    event: Event,
    eventExtraData: EventExtraData?
) {
    var showExpandedMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showHideDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    navigateToEvent(Pair(event, eventExtraData))
                },
                onLongClick = {
                    showExpandedMenu = true
                }
            )
    ) {
        if (eventExtraData?.tag != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = size.width, y = 0f),
                    color = getColorByIndex(eventExtraData.tag),
                    strokeWidth = 20f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (event.typeName != null) {
                Text(
                    text = event.typeName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = event.name.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            if (!isShortEvent) {
                if (!event.groups.isNullOrEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.group),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        event.groups.forEach { group ->
                            Text(
                                text = group.name!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (!event.rooms.isNullOrEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.room),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        event.rooms.forEach { room ->
                            Text(
                                text = room.name!!,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (!event.lecturers.isNullOrEmpty()) {
                    FlowRow(
                        itemVerticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.person),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        event.lecturers.forEach { lecturer ->
                            Text(
                                text = lecturer.shortFio!!,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            if (eventExtraData != null && eventExtraData.comment != "") {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
                Comment(
                    eventExtraData = eventExtraData
                )
            }
        }

        DropdownMenu(
            containerColor = MaterialTheme.colorScheme.background,
            expanded = showExpandedMenu,
            shape = MaterialTheme.shapes.medium,
            onDismissRequest = { showExpandedMenu = false }
        ) {
            DropdownMenuItem(
                colors = MenuDefaults.itemColors().copy(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    leadingIconColor = MaterialTheme.colorScheme.onBackground
                ),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.open_panel),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = LocalContext.current.getString(R.string.open)
                    )
                },
                onClick = {
                    showExpandedMenu = false
                    navigateToEvent(Pair(event, eventExtraData))
                }
            )
            if (isSavedSchedule) {
                DropdownMenuItem(
                    colors = MenuDefaults.itemColors().copy(
                        textColor = MaterialTheme.colorScheme.onBackground,
                        leadingIconColor = MaterialTheme.colorScheme.onBackground
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.visibility_off),
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = LocalContext.current.getString(R.string.hide)
                        )
                    },
                    onClick = {
                        showHideDialog = true
                        showExpandedMenu = false
                    }
                )
            }
            if (event.isCustomEvent && isSavedSchedule) {
                DropdownMenuItem(
                    colors = MenuDefaults.itemColors().copy(
                        textColor = MaterialTheme.colorScheme.error,
                        leadingIconColor = MaterialTheme.colorScheme.error
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.delete),
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = LocalContext.current.getString(R.string.delete)
                        )
                    },
                    onClick = {
                        showExpandedMenu = false
                        showDeleteDialog = true
                    }
                )
            }
        }
        if (showDeleteDialog && event.isCustomEvent) {
            CustomAlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                onConfirmation = {
                    onDeleteEvent(event.id)
                },
                dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                dialogTitle = "${LocalContext.current.getString(R.string.delete_event)}?",
                dialogText = LocalContext.current.getString(R.string.event_deleting_alert)
            )
        }
        if (showHideDialog) {
            CustomAlertDialog(
                onDismissRequest = { showHideDialog = false },
                onConfirmation = {
                    onUpdateHiddenEvent(event.id)
                    showHideDialog = false
                },
                dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
                dialogTitle = "${LocalContext.current.getString(R.string.hide_event)}?",
                dialogText = LocalContext.current.getString(R.string.event_visibility_alert)
            )
        }
    }
}


@Composable
fun Comment(
    eventExtraData: EventExtraData,
    color: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(R.drawable.comment),
            contentDescription = null,
            tint = color ?: MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = eventExtraData.comment,
            style = MaterialTheme.typography.bodyMedium,
            color = color ?: MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}