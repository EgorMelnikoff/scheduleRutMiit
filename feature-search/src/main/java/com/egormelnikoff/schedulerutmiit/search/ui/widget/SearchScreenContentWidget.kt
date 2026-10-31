package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host.AppSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import com.egormelnikoff.schedulerutmiit.search.ui.SearchAction
import com.egormelnikoff.schedulerutmiit.search.ui.SearchState

@Composable
internal fun SearchScreenContentWidget(
   state: SearchState,
   history: List<SearchQuery>,
   snackBarHostState: SnackbarHostState,
   onAction: (SearchAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = {
            AppSnackbarHost(
                snackBarHostState = snackBarHostState
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchHeaderWidget(
                state = state,
                onAction = onAction
            )

            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = state,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { searchState ->
                when {
                    searchState.isLoading -> LoadingScreen()

                    searchState.isEmptyQuery && history.isEmpty() -> {
                        EmptyScreen(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            subtitle = stringResource(R.string.enter_your_query)
                        )
                    }

                    searchState.isEmptyQuery -> {
                        SearchEmptyWidget(
                            history = history,
                            onAction = onAction
                        )
                    }

                    searchState.groups.isEmpty() && searchState.people.isEmpty() ->
                        EmptyScreen(
                            subtitle = stringResource(R.string.nothing_found)
                        )

                    else -> {
                        SearchContentWidget(
                            state = state,
                            contentPadding = contentPadding,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}