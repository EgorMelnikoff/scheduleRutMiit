package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.DecorPreferences
import com.egormelnikoff.schedulerutmiit.core.ui.theme.ScheduleRutMiitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    eventDialog: Route.Dialog.EventDialog
) {
    val eventScreenViewModel =
        hiltViewModel<EventScreenViewModel, EventScreenViewModel.Factory> { factory ->
            factory.create(eventDialog.eventId, eventDialog.date)
        }
    eventScreenViewModel.initScreen()

    val eventScreenState by eventScreenViewModel.state.collectAsStateWithLifecycle()

    EventScreenWidget(
        eventScreenState = eventScreenState,
        onAction = { action ->
            eventScreenViewModel.onAction(action)
        }
    )
}