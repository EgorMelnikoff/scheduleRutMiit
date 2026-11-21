package com.egormelnikoff.schedulerutmiit.news.domain.model

import java.time.LocalDateTime

internal data class NewsShort(
    val id: Long,
    val title: String,
    val date: LocalDateTime,
    val picUrl: String,
    val text: String,
)