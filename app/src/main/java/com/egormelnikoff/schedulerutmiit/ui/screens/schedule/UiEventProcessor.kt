package com.egormelnikoff.schedulerutmiit.ui.screens.schedule

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.view_models.schedule.event.UiEvent
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

@Composable
fun UiEventProcessor(
    scheduleViewModel: ScheduleViewModel,
    settingsViewModel: SettingsViewModel,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settingsViewModel.uiEvent.collect {
            handleUiEvent(it, context, snackBarHostState, useSnackbar = false)
        }
    }

    LaunchedEffect(Unit) {
        scheduleViewModel.uiEvent.collect {
            handleUiEvent(it, context, snackBarHostState, useSnackbar = true)
        }
    }
}

suspend fun handleUiEvent(
    event: UiEvent,
    context: Context,
    snackBarHostState: SnackbarHostState,
    useSnackbar: Boolean
) {
    when (event) {
        is UiEvent.ErrorMessage -> {
            if (useSnackbar) {
                snackBarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Long
                )
            } else {
                Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
            }
        }

        is UiEvent.InfoMessage -> {
            if (useSnackbar) {
                snackBarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
            } else {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}