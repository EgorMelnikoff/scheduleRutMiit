package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.widget.AddEventScreenWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.addEventStateMock

@Composable
fun AddEventScreen(
    editEventDialog: Route.Dialog.EditEventDialog,
) {
    val viewModel =
        hiltViewModel<AddEventViewModel, AddEventViewModel.Factory> { factory ->
            factory.create(editEventDialog.eventId, editEventDialog.scheduleId)
        }
    viewModel.Initialize()
    val addEventState by viewModel.state.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onAction(AddEventAction.OnShowBackDialog(true))
    }

    AddEventScreenWidget(
        state = addEventState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Preview(locale = "ru")
@Composable
fun AddEventScreenPreview() {
    PreviewLightTheme {
        AddEventScreenWidget(addEventStateMock) { }
    }
}
