package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto

sealed interface NewsDetailState {
    data object Loading : NewsDetailState
    data class Error(
        val typedError: TypedError
    ) : NewsDetailState

    data class Loaded(
        val news: NewsParsedDto
    ) : NewsDetailState
}