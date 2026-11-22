package com.egormelnikoff.schedulerutmiit.news.domain.model

internal data class NewsShort(
    val id: Long,
    val title: String,
    val date: String,
    val picUrl: String,
    val text: String,
)