package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.rename_schedule

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.rename_schedule.widget.RenameScheduleScreenWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.renameScheduleStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameScheduleScreen(
    renameDialog: Route.Dialog.RenameNamedScheduleDialog
) {
    val viewModel =
        hiltViewModel<RenameScheduleViewModel, RenameScheduleViewModel.Factory> { factory ->
            factory.create(renameDialog.namedScheduleId)
        }

    viewModel.Initialize()

    val renameScheduleState by viewModel.state.collectAsStateWithLifecycle()

    RenameScheduleScreenWidget(
        renameScheduleState = renameScheduleState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}


@Preview(locale = "ru")
@Composable
fun RenameScheduleScreenPreview() {
    PreviewLightTheme {
        RenameScheduleScreenWidget(renameScheduleStateMock) { }
    }
}