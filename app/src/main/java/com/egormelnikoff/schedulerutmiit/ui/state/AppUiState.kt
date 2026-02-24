package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import kotlinx.coroutines.CoroutineScope

data class AppUiState(
    val appBackStack: AppBackStack,
    val snackBarHostState: SnackbarHostState,
    val newsListState: LazyStaggeredGridState,
    val settingsListState: LazyStaggeredGridState,
    val scope: CoroutineScope,
) {
    companion object {
        @Composable
        operator fun invoke(): AppUiState {
            val appBackStack by remember { mutableStateOf(AppBackStack(startRoute = Route.Page.Schedule)) }
            val snackBarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val newsListState = rememberLazyStaggeredGridState()
            val settingsListState = rememberLazyStaggeredGridState()

            return AppUiState(
                appBackStack = appBackStack,
                snackBarHostState = snackBarHostState,
                scope = scope,
                newsListState = newsListState,
                settingsListState = settingsListState
            )
        }
    }
}