package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.initialize
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget.BottomSheetEventWidget
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget.EventScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.eventStateMock

@Composable
fun EventScreen(
    eventDialog: Route.Dialog.EventDialog
) {
    val viewModel =
        hiltViewModel<EventScreenViewModel, EventScreenViewModel.Factory> { factory ->
            factory.create(eventDialog.eventId, eventDialog.date)
        }
    viewModel.initialize()

    val eventScreenState by viewModel.state.collectAsStateWithLifecycle()

    EventScreenWidget(
        eventScreenState = eventScreenState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )

    if (eventScreenState is EventScreenState.Loaded) {
        val state = eventScreenState as EventScreenState.Loaded
        if (state.showActionsDialog) {
            BottomSheetEventWidget(
                timetableType = state.schedule.timetableType,
                event = state.event,
                onEditEvent = if (state.event.isCustomEvent) {
                    {
                        viewModel.onAction(
                            EventScreenAction.OnEditEventClicked(
                                eventId = state.event.id,
                                scheduleId = state.schedule.id
                            )
                        )
                    }
                } else null,
                onDeleteEvent = if (state.event.isCustomEvent) {
                    {
                        viewModel.onAction(
                            EventScreenAction.OnShowDeleteDialog(true)
                        )
                    }
                } else null,
                onHideEvent = if (!state.event.isHidden) {
                    {
                        viewModel.onAction(EventScreenAction.OnShowHideDialog(true))
                    }
                } else null
            ) {
                viewModel.onAction(EventScreenAction.OnShowMoreDialog(false))
            }
        }
        if (state.showDeleteDialog) {
            AppAlertDialog(
                dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                dialogTitle = "${stringResource(R.string.delete_event)}?",
                dialogText = stringResource(R.string.event_deleting_alert),
                onDismissRequest = {
                    viewModel.onAction(EventScreenAction.OnShowDeleteDialog(false))
                },
                onConfirmation = {
                    viewModel.onAction(
                        EventScreenAction.OnDeleteEvent(
                            state.event.id
                        )
                    )
                }
            )
        }
        if (state.showHideDialog) {
            AppAlertDialog(
                dialogIcon = ImageVector.vectorResource(R.drawable.visibility_off),
                dialogTitle = "${stringResource(R.string.hide_event)}?",
                dialogText = stringResource(R.string.event_visibility_alert),
                onDismissRequest = {
                    viewModel.onAction(EventScreenAction.OnShowHideDialog(false))
                },
                onConfirmation = {
                    viewModel.onAction(
                        EventScreenAction.OnHideEvent(
                            state.event.id
                        )
                    )
                }
            )
        }
    }
}

@Preview(locale = "ru")
@Composable
fun EventScreenPreview() {
    PreviewLightTheme {
        EventScreenWidget(eventStateMock) { }
    }
}