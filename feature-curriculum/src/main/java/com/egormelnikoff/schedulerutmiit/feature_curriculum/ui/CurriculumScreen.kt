package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.dialog.AppDialog
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.widget.CurriculumScreenWidget

@Composable
fun CurriculumScreen() {
    val viewModel = hiltViewModel<CurriculumViewModel>()
    viewModel.Initialize()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    UiEventProcessor(
        viewModel.uiEvent, snackBarHostState
    )

    CurriculumScreenWidget(
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )

    if (state.showAdviceDialog) {
        AppDialog(
            dialogText = stringResource(R.string.search_curriculum_advice),
            painter = painterResource(R.drawable.advice),
            onDismissRequest = {
                viewModel.onAction(
                    CurriculumAction.OnHideAdviceDialog
                )
            }
        )
    }
}