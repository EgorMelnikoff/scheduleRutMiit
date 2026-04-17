package com.egormelnikoff.schedulerutmiit.ui.view_models.news.state

import com.egormelnikoff.schedulerutmiit.data.local.dto.news.NewsParsedDto

data class NewsState(
    val currentNews: NewsParsedDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)