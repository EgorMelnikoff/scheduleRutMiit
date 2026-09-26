package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget.AddEventScreenWidget
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addEventStateMock

@Composable
fun AddEventScreen(
    editEventDialog: Route.Dialog.EditEventDialog,
) {
    val viewModel =
        hiltViewModel<AddEventViewModel, AddEventViewModel.Factory> { factory ->
            factory.create(editEventDialog.eventId, editEventDialog.scheduleId)
        }

    viewModel.initScreen()
    val state by viewModel.state.collectAsStateWithLifecycle()


    AddEventScreenWidget(
        state = state,
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
