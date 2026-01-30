package com.egormelnikoff.schedulerutmiit.view_models.news

import com.egormelnikoff.schedulerutmiit.app.model.News

data class NewsState(
    val currentNews: News? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)