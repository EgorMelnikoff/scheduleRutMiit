package com.egormelnikoff.schedulerutmiit.ui.screen.schedule

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.egormelnikoff.schedulerutmiit.core.ui.elements.AppSnackbarVisuals
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.event.UiEvent
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel

@Composable
fun UiEventProcessor(
    scheduleViewModel: ScheduleViewModel,
    mainViewModel: MainViewModel,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mainViewModel.uiEvent.collect {
            handleUiEvent(it, context, snackBarHostState)
        }
    }

    LaunchedEffect(Unit) {
        scheduleViewModel.uiEvent.collect {
            handleUiEvent(it, context, snackBarHostState)
        }
    }
}

suspend fun handleUiEvent(
    event: UiEvent,
    context: Context,
    snackBarHostState: SnackbarHostState
) {
    when (event) {
        is UiEvent.ErrorMessage -> {
            if (event.useSnackBar) {
                snackBarHostState.showSnackbar(
                    AppSnackbarVisuals(
                        message = event.message,
                        isError = true,
                        duration = SnackbarDuration.Long
                    )
                )
            } else {
                Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
            }
        }

        is UiEvent.InfoMessage -> {
            if (event.useSnackBar) {
                snackBarHostState.showSnackbar(
                    AppSnackbarVisuals(
                        message = event.message,
                        isError = false,
                        duration = SnackbarDuration.Short
                    )
                )
            } else {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}