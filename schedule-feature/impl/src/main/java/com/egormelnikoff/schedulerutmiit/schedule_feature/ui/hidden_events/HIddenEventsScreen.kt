package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.hidden_events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.hidden_events.widget.HiddenEventsScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.eventMock

@Composable
fun HiddenEventsScreen(
    hiddenEventsDialog: Route.Dialog.HiddenEventsDialog
) {
    val viewModel = hiltViewModel<HiddenEventsViewModel, HiddenEventsViewModel.Factory> { factory ->
        factory.create(hiddenEventsDialog.scheduleId)
    }

    val hiddenEvents by viewModel.hiddenEvents.collectAsStateWithLifecycle()

    HiddenEventsScreenWidget(
        hiddenEvents = hiddenEvents,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Preview(locale = "ru")
@Composable
fun HiddenEventsScreenPreview() {
    PreviewLightTheme{
        HiddenEventsScreenWidget(listOf(eventMock, eventMock)) { }
    }
}