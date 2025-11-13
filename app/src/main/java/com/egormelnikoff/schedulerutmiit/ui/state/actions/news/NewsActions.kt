package com.egormelnikoff.schedulerutmiit.ui.state.actions.news

import com.egormelnikoff.schedulerutmiit.view_models.news.NewsViewModel

data class NewsActions(
    val onGetNewsById: (Long) -> Unit, //NewsId
) {
    companion object {
        fun getNewsActions(
            newsViewModel: NewsViewModel
        ) = NewsActions(
            onGetNewsById = { value ->
                newsViewModel.getNewsById(value)
            }
        )
    }
}