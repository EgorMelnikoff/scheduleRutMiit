package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.eventStateMock

@Composable
fun EventScreenWidget(
    eventScreenState: EventScreenState,
    onAction: (EventScreenAction) -> Unit,
) {
    when (eventScreenState) {
        is EventScreenState.Loading -> LoadingScreen()
        is EventScreenState.Loaded -> {
            Scaffold(
                modifier = Modifier.imePadding(),
                topBar = {
                    EventScreenHeaderWidget(
                        eventScreenState = eventScreenState,
                        onAction = onAction
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        EventNameWidget(
                            eventScreenState.schedule.timetableType, eventScreenState.event, 16.dp
                        )
                    }
                    eventGroupsWidget(eventScreenState, onAction)

                    eventRoomsWidget(eventScreenState)

                    eventLecturersWidget(eventScreenState, onAction)

                    eventCommentWidget(eventScreenState, onAction)

                    eventTagWidget(eventScreenState, onAction)

                }
            }
            if (eventScreenState.showActionsDialog) {
                BottomSheetEventWidget(
                    timetableType = eventScreenState.schedule.timetableType,
                    event = eventScreenState.event,
                    onEditEvent = if (eventScreenState.event.isCustomEvent) {
                        {
                            onAction(
                                EventScreenAction.OnEditEventClicked(
                                    eventId = eventScreenState.event.id,
                                    scheduleId = eventScreenState.schedule.id
                                )
                            )
                        }
                    } else null,
                    onDeleteEvent = if (eventScreenState.event.isCustomEvent) {
                        {
                            onAction(
                                EventScreenAction.OnShowDeleteDialog(true)
                            )
                        }
                    } else null,
                    onHideEvent = if (!eventScreenState.event.isHidden) {
                        {
                            onAction(EventScreenAction.OnShowHideDialog(true))
                        }
                    } else null
                ) {
                    onAction(EventScreenAction.OnShowMoreDialog(false))
                }
            }
            if (eventScreenState.showDeleteDialog) {
                CustomAlertDialog(
                    dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                    dialogTitle = "${stringResource(R.string.delete_event)}?",
                    dialogText = stringResource(R.string.event_deleting_alert),
                    onDismissRequest = {
                        onAction(EventScreenAction.OnShowDeleteDialog(false))
                    },
                    onConfirmation = {
                        onAction(
                            EventScreenAction.OnDeleteEvent(
                                eventScreenState.event.id
                            )
                        )
                    }
                )
            }
            if (eventScreenState.showHideDialog) {
                CustomAlertDialog(
                    dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
                    dialogTitle = "${stringResource(R.string.hide_event)}?",
                    dialogText = stringResource(R.string.event_visibility_alert),
                    onDismissRequest = {
                        onAction(EventScreenAction.OnShowHideDialog(false))
                    },
                    onConfirmation = {
                        onAction(
                            EventScreenAction.OnHideEvent(
                                eventScreenState.event.id
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