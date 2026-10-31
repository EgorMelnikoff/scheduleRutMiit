package com.egormelnikoff.schedulerutmiit.search.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.search.ui.widget.SearchScreenContentWidget

@Composable
fun SearchScreen() {
    val viewModel = hiltViewModel<SearchViewModel>()
    viewModel.Initialize()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    UiEventProcessor(
        viewModel.uiEvent, snackBarHostState
    )

    SearchScreenContentWidget(
        state = state,
        history = history,
        snackBarHostState = snackBarHostState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}