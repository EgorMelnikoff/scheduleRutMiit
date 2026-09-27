package com.egormelnikoff.schedulerutmiit.news.ui.news_list

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.egormelnikoff.schedulerutmiit.core.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget.NewsListScreenWidget

@Composable
fun NewsScreen() {
    val viewModel = hiltViewModel<NewsListViewModel>()
    val newsList = viewModel.newsListFlow.collectAsLazyPagingItems()

    NewsListScreenWidget(
        newsList = newsList,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
    StatusBarProtection()
}