package com.egormelnikoff.schedulerutmiit.ui.view_models.news.state

import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto

data class NewsState(
    val currentNews: NewsParsedDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)