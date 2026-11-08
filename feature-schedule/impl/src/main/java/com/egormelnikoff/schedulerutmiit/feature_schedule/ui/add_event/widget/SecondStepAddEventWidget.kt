package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.DateTimeSelectorWidget
import com.egormelnikoff.schedulerutmiit.core.ui.widget.DaySelector
import com.egormelnikoff.schedulerutmiit.core.ui.widget.TimeSelector
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addEventStateMock

@Composable
fun SecondStepAddEventWidget(
    focusManager: FocusManager,
    state: AddEventState.Loaded,
    onAction: (AddEventAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12, Alignment.Top)
    ) {
        if (state.schedule.recurrence != null) {
            DaySelector(
                dateEvent = state.editForm.date,
                onSelectDateEvent = { date ->
                    onAction(AddEventAction.OnDateChanged(date))
                },
                focusManager = focusManager
            )
            TimeSelector(
                focusManager = focusManager,
                startTime = state.editForm.startTime,
                endTime = state.editForm.endTime,
                onShowDialogStartTime = { show ->
                    onAction(AddEventAction.OnShowDialogStartTime(show))
                },
                onShowDialogEndTime = { show ->
                    onAction(AddEventAction.OnShowDialogEndTime(show))
                }
            )
            RecurrenceWidget(
                maxInterval = state.schedule.recurrence.interval,
                currentInterval = state.editForm.interval,
                currentPeriod = state.editForm.period,
                onSelectInterval = { interval ->
                    onAction(AddEventAction.OnIntervalChanged(interval))
                },
                onSelectPeriod = { period ->
                    onAction(AddEventAction.OnPeriodChanged(period))
                },
            )
        } else {
            DateTimeSelectorWidget(
                focusManager = focusManager,
                dateEvent = state.editForm.date,
                startTime = state.editForm.startTime,
                endTime = state.editForm.endTime,
                onShowDialogDate = { show ->
                    onAction(AddEventAction.OnShowDialogDate(show))
                },
                onShowDialogStartTime = { show ->
                    onAction(AddEventAction.OnShowDialogStartTime(show))
                },
                onShowDialogEndTime = { show ->
                    onAction(AddEventAction.OnShowDialogEndTime(show))
                }
            )
        }
    }
}

@Preview(locale = "ru", showBackground = true)
@Composable
fun SecondStepAddEventWidgetPreview() {
    PreviewLightTheme {
        SecondStepAddEventWidget(LocalFocusManager.current, addEventStateMock) { }
    }
}