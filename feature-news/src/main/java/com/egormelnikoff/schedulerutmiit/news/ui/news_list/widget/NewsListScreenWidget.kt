package com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsListAction

@Composable
fun NewsListScreenWidget(
    newsList: LazyPagingItems<NewsShortDto>,
    onAction: (NewsListAction) -> Unit
) {
    when (newsList.loadState.refresh) {
        is LoadState.Loading -> NewsListLoadingWidget()

        is LoadState.Error -> NewsListErrorWidget(newsList)

        is LoadState.NotLoading -> NewsListContentWidget(
            newsList = newsList,
            onAction = onAction
        )
    }
}