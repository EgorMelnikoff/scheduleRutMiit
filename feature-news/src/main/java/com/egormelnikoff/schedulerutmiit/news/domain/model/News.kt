package com.egormelnikoff.schedulerutmiit.news.domain.model

internal data class News(
    val title: String,
    val date: String,
    val paragraphs: List<Pair<String, Any>>,
    val images: List<String>
)