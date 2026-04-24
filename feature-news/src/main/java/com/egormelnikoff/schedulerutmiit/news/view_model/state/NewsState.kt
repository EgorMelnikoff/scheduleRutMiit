package com.egormelnikoff.schedulerutmiit.news.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.dto.news.NewsParsedDto

data class NewsState(
    val currentNews: NewsParsedDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)