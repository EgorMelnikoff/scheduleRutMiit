package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.PagerScreenContainer
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addEventStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreenWidget(
    state: AddEventState,
    onAction: (AddEventAction) -> Unit
) {
    when (state) {
        is AddEventState.Loaded -> {
            var showBackDialog by remember { mutableStateOf(false) }

            BackHandler {
                showBackDialog = true
            }

            val focusManager = LocalFocusManager.current

            Scaffold(
                topBar = {
                    CustomTopAppBar(
                        titleText = if (state.editForm.isEdit) stringResource(R.string.editing)
                        else stringResource(R.string.create_class),
                        navAction = {
                            onAction(AddEventAction.OnBackClicked)
                        }
                    )
                }
            ) { innerPadding ->
                val pagerState = rememberPagerState(
                    initialPage = 0
                ) { 3 }

                PagerScreenContainer(
                    pagerState = pagerState,
                    isNextEnabled = { page ->
                        when (page) {
                            0 -> state.editForm.name.isNotBlank()
                            1 -> state.editForm.date != null && state.editForm.startTime != null && state.editForm.endTime != null && (state.schedule.recurrence != null && state.editForm.interval != -1 || state.schedule.recurrence == null)
                            2 -> state.editForm.isValid
                            else -> true
                        }
                    },
                    onFinish = {
                        state.editForm.date?.let {
                            val startDateTime =
                                state.editForm.startTime?.toUtcDateTime(it)
                            val endDateTime =
                                state.editForm.endTime?.toUtcDateTime(it)
                            if (startDateTime != null && endDateTime != null) {
                                onAction(AddEventAction.OnSaveClicked)
                            }
                        }
                        onAction(AddEventAction.OnBackClicked)
                    },
                    paddingValues = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = innerPadding.calculateTopPadding() + 12.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                    finishTitle = if (state.editForm.isEdit) {
                        stringResource(R.string.save)
                    } else {
                        stringResource(R.string.create)
                    }
                ) { page ->
                    when (page) {
                        0 -> FirstStepAddEventWidget(
                            focusManager = focusManager,
                            state = state,
                            onAction = onAction
                        )

                        1 -> SecondStepAddEventWidget(
                            focusManager = focusManager,
                            state = state,
                            onAction = onAction
                        )

                        2 -> ThirdStepAddEventWidget(
                            focusManager = focusManager,
                            state = state,
                            onAction = onAction
                        )
                    }
                }

                if (state.showDialogDate) {
                    BottomSheetDatePicker(
                        selectedDate = state.editForm.date,
                        onDateSelect = { date -> onAction(AddEventAction.OnDateChanged(date)) },
                        onShowDialog = { show -> onAction(AddEventAction.OnShowDialogDate(show)) },
                        startDate = state.schedule.startDate,
                        endDate = state.schedule.endDate
                    )
                }
                if (state.showDialogStartTime) {
                    BottomSheetTimePicker(
                        selectedTime = state.editForm.startTime,
                        onTimeSelect = { time ->
                            onAction(AddEventAction.OnStartTimeChanged(time))
                            if (state.editForm.startTime == null) {
                                onAction(
                                    AddEventAction.OnEndTimeChanged(
                                        time.plusHours(1)
                                            .plusMinutes(20)
                                    )
                                )
                            }
                        },
                        onShowDialog = { show -> onAction(AddEventAction.OnShowDialogStartTime(show)) },
                    )
                }
                if (state.showDialogEndTime) {
                    BottomSheetTimePicker(
                        selectedTime = state.editForm.endTime,
                        onTimeSelect = { time ->
                            onAction(
                                AddEventAction.OnEndTimeChanged(time)
                            )
                        },
                        onShowDialog = { show -> onAction(AddEventAction.OnShowDialogEndTime(show)) }
                    )
                }

                if (showBackDialog) {
                    CustomAlertDialog(
                        dialogTitle = stringResource(R.string.exit),
                        dialogText = stringResource(R.string.exit_message),
                        onDismissRequest = {
                            showBackDialog = false
                        },
                        onConfirmation = {
                            onAction(AddEventAction.OnBackClicked)
                        }
                    )
                }
            }
        }

        is AddEventState.Loading -> LoadingScreen()
    }
}


@Preview(locale = "ru")
@Composable
fun EventScreenWidgetPreview() {
    PreviewLightTheme {
        AddEventScreenWidget(addEventStateMock) { }
    }
}