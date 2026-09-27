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
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsListAction

@Composable
fun NewsListContentWidget(
    newsList: LazyPagingItems<NewsShortDto>,
    onAction: (NewsListAction) -> Unit
) {
    val newsListState = rememberLazyStaggeredGridState()

    Scaffold { contentPadding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
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