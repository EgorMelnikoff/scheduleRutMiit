package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.event

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.EventView
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex

@Composable
fun ScheduleSingleEvent(
    navigateToEvent: (Schedule, Boolean, Event, EventExtraData?) -> Unit,
    navigateToEditEvent: (Schedule, Event) -> Unit,
    onDeleteEvent: (Schedule, Event) -> Unit,
    onUpdateHiddenEvent: (Schedule, Event) -> Unit,
    schedule: Schedule,
    isSavedSchedule: Boolean,
    eventView: EventView,
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
                    navigateToEvent(
                        schedule,
                        isSavedSchedule,
                        event,
                        eventExtraData
                    )
                },
                onLongClick = {
                    showExpandedMenu = true
                }
            )
    ) {
        if (eventView.tagVisible && eventExtraData?.tag != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = size.width, y = 0f),
                    color = eventExtraData.tag.getColorByIndex(),
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
            event.typeName?.let {
                Text(
                    text = requireNotNull(event.typeName),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            if (eventView.groupsVisible && !event.groups.isNullOrEmpty()) {
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
                    event.groups?.forEach { group ->
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }

            if (eventView.roomsVisible && !event.rooms.isNullOrEmpty()) {
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
                    event.rooms?.forEach { room ->
                        Text(
                            text = room.name,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }

            if (eventView.lecturersVisible && !event.lecturers.isNullOrEmpty()) {
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
                    event.lecturers?.forEach { lecturer ->
                        Text(
                            text = lecturer.shortFio,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }

            if (eventView.commentVisible && eventExtraData != null && eventExtraData.comment.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
                Text(
                    text = eventExtraData.comment.replace(Regex("\\s+"), " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
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
                        text = stringResource(R.string.open)
                    )
                },
                onClick = {
                    showExpandedMenu = false
                    navigateToEvent(
                        schedule,
                        isSavedSchedule,
                        event,
                        eventExtraData
                    )
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
                            text = stringResource(R.string.hide)
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
                        textColor = MaterialTheme.colorScheme.onBackground,
                        leadingIconColor = MaterialTheme.colorScheme.onBackground
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.edit),
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.edit)
                        )
                    },
                    onClick = {
                        navigateToEditEvent(schedule, event)
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
                            text = stringResource(R.string.delete)
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
                    onDeleteEvent(schedule, event)
                },
                dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                dialogTitle = "${stringResource(R.string.delete_event)}?",
                dialogText = stringResource(R.string.event_deleting_alert)
            )
        }
        if (showHideDialog) {
            CustomAlertDialog(
                onDismissRequest = { showHideDialog = false },
                onConfirmation = {
                    onUpdateHiddenEvent(schedule, event)
                    showHideDialog = false
                },
                dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
                dialogTitle = "${stringResource(R.string.hide_event)}?",
                dialogText = stringResource(R.string.event_visibility_alert)
            )
        }
    }
}