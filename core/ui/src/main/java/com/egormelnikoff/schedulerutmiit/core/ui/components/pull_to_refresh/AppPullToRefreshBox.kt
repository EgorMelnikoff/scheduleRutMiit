package com.egormelnikoff.schedulerutmiit.core.ui.components.pull_to_refresh

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun AppPullToRefreshBox(
    modifier: Modifier,
    isRefreshing: Boolean,
    enabled: Boolean = true,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = {
            if (enabled) {
                onRefresh()
            }
        },
        state = state,
        indicator = {
            if (enabled) {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    containerColor = AppTheme.colors.secondaryContainer,
                    color = AppTheme.colors.primary,
                    state = state
                )
            }
        },
    ) {
        content.invoke()
    }
}