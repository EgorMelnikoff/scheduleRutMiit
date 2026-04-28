package com.egormelnikoff.schedulerutmiit.core.network.dto.news

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsListDto(
    @SerialName("maxPage")
    val maxPage: Int,
    @SerialName("items")
    val items: List<NewsShortDto>
)