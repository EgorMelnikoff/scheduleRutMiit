package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet.BottomSheetDateRangePicker
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addNonPeriodicScheduleStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreenWidget(
    addScheduleState: AddScheduleState,
    onAction: (AddScheduleAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppTopBar(
                titleText = stringResource(R.string.create_schedule),
                navAction = {
                    onAction(AddScheduleAction.OnShowBackDialog(true))
                }
            )
        }
    ) { contentPadding ->
        ScaffoldLazyColumn(
            contentPadding = contentPadding,
            itemSpacing = 12.dp
        ) {
            item {
                AddScheduleInfoWidget(
                    focusManager = focusManager,
                    addScheduleState = addScheduleState,
                    onAction = onAction
                )
            }

            item {
                AddScheduleTypeWidget(
                    addScheduleState = addScheduleState,
                    onAction = onAction
                )
            }

            item {
                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    buttonTitle = stringResource(R.string.create),
                    enabled = addScheduleState.isButtonEnabled,
                    onClick = {
                        onAction(AddScheduleAction.OnSaveClicked)
                    }
                )
            }
        }
    }

    if (addScheduleState.showDialogDate) {
        BottomSheetDateRangePicker(
            selectedStartDate = addScheduleState.startDate,
            selectedEndDate = addScheduleState.endDate,
            onDateSelect = { startDate, endDate ->
                onAction(AddScheduleAction.OnUpdateDates(startDate, endDate))
            },
            onShowDialog = { show ->
                onAction(AddScheduleAction.OnShowDialogDate(show))
            }
        )
    }

    if (addScheduleState.showBackDialog) {
        AppAlertDialog(
            dialogTitle = stringResource(R.string.exit),
            dialogText = stringResource(R.string.exit_message),
            onDismissRequest = {
                onAction(AddScheduleAction.OnShowBackDialog(false))
            },
            onConfirmation = {
                onAction(AddScheduleAction.OnBackClicked)
            }
        )
    }
}


@Preview(locale = "ru")
@Composable
fun AddScheduleScreenWidgetPreview() {
    PreviewLightTheme {
        AddScheduleScreenWidget(addNonPeriodicScheduleStateMock) { }
    }
}
