package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget.ScheduleScreenWidget

@Composable
fun ScreenSchedule(
    contentPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
) {
    val viewModel = hiltViewModel<ScheduleViewModel>()
    viewModel.Initialize()

    val state by viewModel.state.collectAsStateWithLifecycle()

    UiEventProcessor(
        viewModel.uiEvent,
        snackbarHostState
    )

    ScheduleScreenWidget(
        state = state,
        contentPadding = contentPadding,
        onAction = viewModel::onAction
    )
}
