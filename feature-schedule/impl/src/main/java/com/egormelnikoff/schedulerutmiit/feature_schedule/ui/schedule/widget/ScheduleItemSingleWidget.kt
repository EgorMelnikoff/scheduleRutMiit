package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

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
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppAlertBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData

@Composable
fun ScheduleItemSingleWidget(
    navigateToEvent: (Route.Dialog.EventDialog) -> Unit,
    navigateToEditEvent: (Route.Dialog.EditEventDialog) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onUpdateHiddenEvent: (Long) -> Unit,
    eventDialog: Route.Dialog.EventDialog,
    editEventDialog: Route.Dialog.EditEventDialog,
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
                enabled = isSavedSchedule,
                onClick = {
                    navigateToEvent(eventDialog)
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
                .padding(AppTheme.paddings.padding12),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding6)
        ) {
            event.typeName?.let {
                Text(
                    text = event.typeName,
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.onSecondaryContainer
                )
            }
            Text(
                text = event.name,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            if (eventView.groupsVisible && !event.groups.isNullOrEmpty()) {
                FlowRow(
                    itemVerticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding6, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(14.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.group),
                        contentDescription = null,
                        tint = AppTheme.colors.onSecondaryContainer
                    )
                    event.groups.forEach { group ->
                        Text(
                            text = group.name,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.onSecondaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }

            if (eventView.roomsVisible && !event.rooms.isNullOrEmpty()) {
                FlowRow(
                    itemVerticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding6, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(14.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.room),
                        contentDescription = null,
                        tint = AppTheme.colors.onSecondaryContainer
                    )
                    event.rooms.forEach { room ->
                        Text(
                            text = room.name,
                            color = AppTheme.colors.onSecondaryContainer,
                            style = AppTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }

            if (eventView.lecturersVisible && !event.lecturers.isNullOrEmpty()) {
                FlowRow(
                    itemVerticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding6, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(14.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.person),
                        contentDescription = null,
                        tint = AppTheme.colors.onSecondaryContainer
                    )
                    event.lecturers.forEach { lecturer ->
                        Text(
                            text = lecturer.shortFio,
                            color = AppTheme.colors.onSecondaryContainer,
                            style = AppTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }

            if (eventView.commentVisible && eventExtraData != null && eventExtraData.comment.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = AppTheme.paddings.padding4),
                    color = AppTheme.colors.outline,
                    thickness = 0.5.dp
                )
                Text(
                    text = eventExtraData.comment.replace(Regex("\\s+"), " "),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }
        }

        DropdownMenu(
            containerColor = AppTheme.colors.background,
            expanded = showExpandedMenu,
            shape = AppTheme.shapes.medium,
            onDismissRequest = { showExpandedMenu = false }
        ) {
            DropdownMenuItem(
                colors = MenuDefaults.itemColors().copy(
                    textColor = AppTheme.colors.onBackground,
                    leadingIconColor = AppTheme.colors.onBackground
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

            if (event.isCustomEvent) {
                DropdownMenuItem(
                    colors = MenuDefaults.itemColors().copy(
                        textColor = AppTheme.colors.onBackground,
                        leadingIconColor = AppTheme.colors.onBackground
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
                        navigateToEditEvent(editEventDialog)
                        showExpandedMenu = false
                    }
                )
                DropdownMenuItem(
                    colors = MenuDefaults.itemColors().copy(
                        textColor = AppTheme.colors.error,
                        leadingIconColor = AppTheme.colors.error
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
            AppAlertBottomSheet(
                onDismissRequest = { showDeleteDialog = false },
                onConfirmation = {
                    onDeleteEvent(event.id)
                },
                dialogTitle = "${stringResource(R.string.delete_event)}?",
                dialogText = stringResource(R.string.event_deleting_alert)
            )
        }

        if (showHideDialog) {
            AppAlertBottomSheet(
                onDismissRequest = { showHideDialog = false },
                onConfirmation = {
                    onUpdateHiddenEvent(event.id)
                    showHideDialog = false
                },
                dialogTitle = "${stringResource(R.string.hide_event)}?",
                dialogText = stringResource(R.string.event_visibility_alert)
            )
        }
    }
}