package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.egormelnikoff.schedulerutmiit.core.ui.event.UiEvent
import com.egormelnikoff.schedulerutmiit.core.ui.event.handleUiEvent
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun UiEventProcessor(
    uiEvent: SharedFlow<UiEvent>,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            event.handleUiEvent(context, snackBarHostState)
        }
    }
}