package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet.BottomSheetDateRangePicker
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.widget.AddScheduleScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addNonPeriodicScheduleStateMock

@Composable
fun AddScheduleScreen() {
    val viewModel = hiltViewModel<AddScheduleViewModel>()
    val addScheduleState = viewModel.state.collectAsStateWithLifecycle().value

    BackHandler {
        viewModel.onAction(AddScheduleAction.OnShowBackDialog(true))
    }

    AddScheduleScreenWidget(
        addScheduleState = addScheduleState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )

    if (addScheduleState.showDialogDate) {
        BottomSheetDateRangePicker(
            selectedStartDate = addScheduleState.startDate,
            selectedEndDate = addScheduleState.endDate,
            onDateSelect = { startDate, endDate ->
                viewModel.onAction(AddScheduleAction.OnUpdateDates(startDate, endDate))
            },
            onShowDialog = { show ->
                viewModel.onAction(AddScheduleAction.OnShowDialogDate(show))
            }
        )
    }

    if (addScheduleState.showBackDialog) {
        AppAlertDialog(
            dialogTitle = stringResource(R.string.exit),
            dialogText = stringResource(R.string.exit_message),
            onDismissRequest = {
                viewModel.onAction(AddScheduleAction.OnShowBackDialog(false))
            },
            onConfirmation = {
                viewModel.onAction(AddScheduleAction.OnBackClicked)
            }
        )
    }
}

@Preview(locale = "ru")
@Composable
fun AddScheduleScreenPreview() {
    PreviewLightTheme {
        AddScheduleScreenWidget(addNonPeriodicScheduleStateMock) { }
    }
}