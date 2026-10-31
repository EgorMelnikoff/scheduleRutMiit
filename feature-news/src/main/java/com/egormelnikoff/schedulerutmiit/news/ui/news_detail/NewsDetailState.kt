package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

import com.egormelnikoff.schedulerutmiit.core.common.utils.error.AppError
import com.egormelnikoff.schedulerutmiit.news.domain.model.News

internal sealed interface NewsDetailState {
    data object Loading : NewsDetailState
    data class Error(
        val appError: AppError
    ) : NewsDetailState

    data class Loaded(
        val news: News
    ) : NewsDetailState
}