package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.initialize
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget.NewsDetailScreenWidget

@Composable
fun NewsDialog(
    newsDialog: Route.Dialog.NewsDialog,
) {
    val viewModel =
        hiltViewModel<NewsDetailViewModel, NewsDetailViewModel.Factory> { factory ->
            factory.create(newsDialog.newsId)
        }

    viewModel.initialize()

    val newsDetailState by viewModel.state.collectAsStateWithLifecycle()

    NewsDetailScreenWidget(
        newsDetailState = newsDetailState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
    StatusBarProtection()
}