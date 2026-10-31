package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event.widget.EventScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.eventStateMock

@Composable
fun EventScreen(
    eventDialog: Route.Dialog.EventDialog
) {
    val viewModel =
        hiltViewModel<EventViewModel, EventViewModel.Factory> { factory ->
            factory.create(eventDialog.eventId, eventDialog.date)
        }
    viewModel.Initialize()

    val eventScreenState by viewModel.state.collectAsStateWithLifecycle()

    EventScreenWidget(
        eventState = eventScreenState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Preview(locale = "ru")
@Composable
fun EventScreenPreview() {
    PreviewLightTheme {
        EventScreenWidget(eventStateMock) { }
    }
}