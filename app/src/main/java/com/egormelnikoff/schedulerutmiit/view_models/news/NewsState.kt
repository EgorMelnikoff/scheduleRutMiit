package com.egormelnikoff.schedulerutmiit.view_models.news

import com.egormelnikoff.schedulerutmiit.app.network.model.NewsContent

data class NewsState(
    val currentNews: NewsContent? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)