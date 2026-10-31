package com.egormelnikoff.schedulerutmiit.news.domain.model

internal data class NewsList(
    val maxPage: Int,
    val items: List<NewsShort>
)