package com.egormelnikoff.schedulerutmiit.view_models.news.state

import com.egormelnikoff.schedulerutmiit.app.dto.local.news.NewsParsedDto

data class NewsState(
    val currentNews: NewsParsedDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)