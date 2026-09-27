package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.time_picker.AppTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.initialize
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget.AddEventScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addEventStateMock

@Composable
fun AddEventScreen(
    editEventDialog: Route.Dialog.EditEventDialog,
) {
    val viewModel =
        hiltViewModel<AddEventViewModel, AddEventViewModel.Factory> { factory ->
            factory.create(editEventDialog.eventId, editEventDialog.scheduleId)
        }
    viewModel.initialize()
    val addEventState by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onAction(AddEventAction.OnShowBackDialog(true))
    }

    AddEventScreenWidget(
        state = addEventState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )

    if (addEventState is AddEventState.Loaded) {
        val state = addEventState as AddEventState.Loaded

        if (state.showDialogDate) {
            BottomSheetDatePicker(
                selectedDate = state.editForm.date,
                onDateSelect = { date -> viewModel.onAction(AddEventAction.OnDateChanged(date)) },
                onShowDialog = { show -> viewModel.onAction(AddEventAction.OnShowDialogDate(show)) },
                startDate = state.schedule.startDate,
                endDate = state.schedule.endDate
            )
        }
        if (state.showDialogStartTime) {
            AppTimePicker(
                selectedTime = state.editForm.startTime,
                onTimeSelect = { time ->
                    viewModel.onAction(AddEventAction.OnStartTimeChanged(time))
                    if (state.editForm.startTime == null) {
                        viewModel.onAction(
                            AddEventAction.OnEndTimeChanged(
                                time.plusHours(1)
                                    .plusMinutes(20)
                            )
                        )
                    }
                },
                onShowDialog = { show ->
                    viewModel.onAction(
                        AddEventAction.OnShowDialogStartTime(
                            show
                        )
                    )
                },
            )
        }
        if (state.showDialogEndTime) {
            AppTimePicker(
                selectedTime = state.editForm.endTime,
                onTimeSelect = { time ->
                    viewModel.onAction(
                        AddEventAction.OnEndTimeChanged(time)
                    )
                },
                onShowDialog = { show -> viewModel.onAction(AddEventAction.OnShowDialogEndTime(show)) }
            )
        }
        if (state.showBackDialog) {
            AppAlertDialog(
                dialogTitle = stringResource(R.string.exit),
                dialogText = stringResource(R.string.exit_message),
                onDismissRequest = {
                    viewModel.onAction(AddEventAction.OnShowBackDialog(false))
                },
                onConfirmation = {
                    viewModel.onAction(AddEventAction.OnBackClicked)
                }
            )
        }
    }
}

@Preview(locale = "ru")
@Composable
fun AddEventScreenPreview() {
    PreviewLightTheme {
        AddEventScreenWidget(addEventStateMock) { }
    }
}
