package com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.AppShimmerCard
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
internal fun NewsListLoadingWidget() {
    Scaffold { paddingValues ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            verticalItemSpacing = AppTheme.paddings.padding12,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12),
            userScrollEnabled = false,
            contentPadding = PaddingValues(
                start = AppTheme.paddings.padding16,
                end = AppTheme.paddings.padding16,
                top = paddingValues.calculateTopPadding() + AppTheme.paddings.padding16,
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            items(20) {
                AppShimmerCard(
                    height = 200.dp
                )
            }
        }
    }
}