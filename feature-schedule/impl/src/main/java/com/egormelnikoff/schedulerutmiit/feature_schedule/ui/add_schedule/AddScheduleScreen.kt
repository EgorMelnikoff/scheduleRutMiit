package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.widget.AddScheduleScreenWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addNonPeriodicScheduleStateMock

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
}

@Preview(locale = "ru")
@Composable
fun AddScheduleScreenPreview() {
    PreviewLightTheme {
        AddScheduleScreenWidget(addNonPeriodicScheduleStateMock) { }
    }
}