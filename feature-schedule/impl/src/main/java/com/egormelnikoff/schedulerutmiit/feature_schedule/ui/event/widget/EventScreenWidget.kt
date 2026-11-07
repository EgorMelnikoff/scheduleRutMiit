package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.eventStateMock

@Composable
fun EventScreenWidget(
    eventState: EventState,
    onAction: (EventAction) -> Unit,
) {
    when (eventState) {
        is EventState.Loading -> LoadingScreen()
        is EventState.Loaded -> {
            Scaffold(
                modifier = Modifier.imePadding(),
                topBar = {
                    EventHeaderWidget(
                        eventState = eventState,
                        onAction = onAction
                    )
                }
            ) { contentPadding ->
                ScaffoldLazyColumn(
                    contentPadding = contentPadding,
                    horizontalSpacing = 0.dp,
                    itemSpacing = 12.dp
                ) {
                    item {
                        EventNameWidget(
                            eventState.schedule.timetableType, eventState.event, 16.dp
                        )
                    }
                    eventGroupsWidget(eventState, onAction)

                    eventRoomsWidget(eventState)

                    eventLecturersWidget(eventState, onAction)

                    eventCommentWidget(eventState, onAction)

                    eventTagWidget(eventState, onAction)
                }
            }

            if (eventState.showActionsDialog) {
                BottomSheetEventWidget(
                    timetableType = eventState.schedule.timetableType,
                    event = eventState.event,
                    onEditEvent = if (eventState.event.isCustomEvent) {
                        {
                            onAction(
                                EventAction.OnEditEventClicked(
                                    eventId = eventState.event.id,
                                    scheduleId = eventState.schedule.id
                                )
                            )
                        }
                    } else null,
                    onDeleteEvent = if (eventState.event.isCustomEvent) {
                        {
                            onAction(
                                EventAction.OnShowDeleteDialog(true)
                            )
                        }
                    } else null,
                    onHideEvent = if (!eventState.event.isHidden) {
                        {
                            onAction(EventAction.OnShowHideDialog(true))
                        }
                    } else null
                ) {
                    onAction(EventAction.OnShowMoreDialog(false))
                }
            }
            if (eventState.showDeleteDialog) {
                AppAlertDialog(
                    dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                    dialogTitle = "${stringResource(R.string.delete_event)}?",
                    dialogText = stringResource(R.string.event_deleting_alert),
                    onDismissRequest = {
                        onAction(EventAction.OnShowDeleteDialog(false))
                    },
                    onConfirmation = {
                        onAction(
                            EventAction.OnDeleteEvent(
                                eventState.event.id
                            )
                        )
                    }
                )
            }
            if (eventState.showHideDialog) {
                AppAlertDialog(
                    dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
                    dialogTitle = "${stringResource(R.string.hide_event)}?",
                    dialogText = stringResource(R.string.event_visibility_alert),
                    onDismissRequest = {
                        onAction(EventAction.OnShowHideDialog(false))
                    },
                    onConfirmation = {
                        onAction(
                            EventAction.OnHideEvent(
                                eventState.event.id
                            )
                        )
                    }
                )
            }
        }
    }
}


@Preview(locale = "ru")
@Composable
fun EventScreenWidgetPreview() {
    PreviewLightTheme{
        EventScreenWidget(eventStateMock) { }
    }
}