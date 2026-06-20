package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.egormelnikoff.schedulerutmiit.core.ui.event.handleUiEvent
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel

@Composable
fun UiEventProcessor(
    scheduleViewModel: ScheduleViewModel,
    mainViewModel: MainViewModel,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mainViewModel.uiEvent.collect { event ->
            event.handleUiEvent(
                context = context,
                snackBarHostState = snackBarHostState
            )
        }
    }

    LaunchedEffect(Unit) {
        scheduleViewModel.uiEvent.collect { event ->
            event.handleUiEvent(
                context = context,
                snackBarHostState = snackBarHostState
            )
        }
    }
}