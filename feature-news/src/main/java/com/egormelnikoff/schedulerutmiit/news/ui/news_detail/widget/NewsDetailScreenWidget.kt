package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.runtime.Composable
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDetailAction
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDetailState

@Composable
fun NewsDetailScreenWidget(
    newsDetailState: NewsDetailState,
    onAction: (NewsDetailAction) -> Unit
) {
    when (newsDetailState) {
        is NewsDetailState.Loading -> LoadingScreen()
        is NewsDetailState.Error -> NewsDetailErrorWidget(
            newsDetailState = newsDetailState,
            onAction = onAction
        )

        is NewsDetailState.Loaded -> NewsDetailContentWidget(
            news = newsDetailState.news
        )
    }
}