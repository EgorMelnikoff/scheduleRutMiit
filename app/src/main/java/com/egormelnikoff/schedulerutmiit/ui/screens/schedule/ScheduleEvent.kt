package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeYellow
import java.time.ZoneId
import java.time.ZoneOffset

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (events.first().timeSlotName != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = events.first().timeSlotName.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text =
                    "${
                        events.first().startDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime()
                    } - ${
                        events.first().endDatetime!!
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalTime()
                    }",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (event.typeName != null) {
                Text(
                    text = event.typeName,
                    fontSize = 12.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = event.name.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )

                        event.groups.forEach { group ->
                            Text(
                                text = group.name!!,
                                fontSize = 12.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        event.rooms.forEach { room ->
                            Text(
                                text = room.name!!,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                overflow = TextOverflow.Ellipsis,
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        event.lecturers.forEach { lecturer ->
                            Text(
                                text = lecturer.shortFio!!,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                overflow = TextOverflow.Ellipsis,
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
            shape = RoundedCornerShape(12.dp),
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
                onClick = { navigateToEvent(Pair(event, eventExtraData)) }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (eventExtraData.tag != 0) {
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
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = ImageVector.vectorResource(R.drawable.circle),
                contentDescription = null,
                tint = color
            )
        }
        Text(
            text = eventExtraData.comment,
            fontSize = 12.sp,
            maxLines = 2,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            overflow = TextOverflow.Ellipsis,
            color = color ?: MaterialTheme.colorScheme.onBackground
        )
    }
}