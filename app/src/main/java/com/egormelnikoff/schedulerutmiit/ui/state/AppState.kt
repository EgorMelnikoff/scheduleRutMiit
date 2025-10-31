package com.egormelnikoff.schedulerutmiit.ui.state

import android.content.Context
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Routes
import kotlinx.coroutines.CoroutineScope

data class AppState(
    val appBackStack: AppBackStack<Routes>,
    val snackBarHostState: SnackbarHostState,
    val focusManager: FocusManager,
    val uriHandler: UriHandler,
    val context: Context,
    val scope: CoroutineScope,
    val newsListState: LazyStaggeredGridState,
    val settingsListState: LazyStaggeredGridState
)

@Composable
fun rememberAppState(): AppState {
    val appBackStack by remember { mutableStateOf(AppBackStack<Routes>(startRoute = Routes.Schedule)) }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val newsListState = rememberLazyStaggeredGridState()
    val settingsListState = rememberLazyStaggeredGridState()

    return AppState(
        appBackStack = appBackStack,
        snackBarHostState = snackBarHostState,
        focusManager = focusManager,
        uriHandler = uriHandler,
        context = context,
        scope = scope,
        newsListState = newsListState,
        settingsListState = settingsListState
    )

}