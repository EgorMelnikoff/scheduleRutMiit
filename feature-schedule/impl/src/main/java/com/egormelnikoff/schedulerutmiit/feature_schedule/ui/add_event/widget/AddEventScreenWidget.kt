package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppAlertBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.components.time_picker.AppTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.containers.PagerScreenContainer
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addEventStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreenWidget(
    state: AddEventState,
    onAction: (AddEventAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppTopBar(
                titleText = if (state is AddEventState.Loaded && state.editForm.isEdit) stringResource(
                    R.string.editing
                )
                else stringResource(R.string.create_class),
                navAction = {
                    onAction(AddEventAction.OnShowBackDialog(true))
                }
            )
        }
    ) { contentPadding ->
        when (state) {
            is AddEventState.Loaded -> {
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
                        start = AppTheme.paddings.padding16,
                        end = AppTheme.paddings.padding16,
                        top = contentPadding.calculateTopPadding() + AppTheme.paddings.padding12,
                        bottom = contentPadding.calculateBottomPadding()
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
                        onDateSelect = { date ->
                            onAction(
                                AddEventAction.OnDateChanged(
                                    date
                                )
                            )
                        },
                        onShowDialog = { show ->
                            onAction(
                                AddEventAction.OnShowDialogDate(
                                    show
                                )
                            )
                        },
                        startDate = state.schedule.startDate,
                        endDate = state.schedule.endDate
                    )
                }
                if (state.showDialogStartTime) {
                    AppTimePicker(
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
                        onShowDialog = { show ->
                            onAction(
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
                            onAction(
                                AddEventAction.OnEndTimeChanged(time)
                            )
                        },
                        onShowDialog = { show ->
                            onAction(
                                AddEventAction.OnShowDialogEndTime(
                                    show
                                )
                            )
                        }
                    )
                }
                if (state.showBackDialog) {
                    AppAlertBottomSheet(
                        dialogTitle = stringResource(R.string.exit),
                        dialogText = stringResource(R.string.exit_message),
                        onDismissRequest = {
                            onAction(AddEventAction.OnShowBackDialog(false))
                        },
                        onConfirmation = {
                            onAction(AddEventAction.OnBackClicked)
                        }
                    )
                }

            }

            is AddEventState.Loading -> LoadingScreen()
        }
    }
}


@Preview(locale = "ru")
@Composable
fun EventScreenWidgetPreview() {
    PreviewLightTheme {
        AddEventScreenWidget(addEventStateMock) { }
    }
}