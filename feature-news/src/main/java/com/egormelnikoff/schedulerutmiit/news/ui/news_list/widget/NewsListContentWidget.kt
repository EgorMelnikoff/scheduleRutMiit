package com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsShort
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsListAction

@Composable
internal fun NewsListContentWidget(
    newsList: LazyPagingItems<NewsShort>,
    onAction: (NewsListAction) -> Unit
) {
    val newsListState = rememberLazyStaggeredGridState()

    Scaffold { contentPadding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            verticalItemSpacing = AppTheme.paddings.padding12,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12),
            contentPadding = PaddingValues(
                start = AppTheme.paddings.padding16,
                end = AppTheme.paddings.padding16,
                top = contentPadding.calculateTopPadding() + AppTheme.paddings.padding16,
                bottom = contentPadding.calculateBottomPadding()
            ),
            state = newsListState
        ) {
            newsListItemsWidget(newsList, onAction)

            if (newsList.loadState.append is LoadState.Loading) {
                newsListLoadingItem()
            }
        }
    }
}