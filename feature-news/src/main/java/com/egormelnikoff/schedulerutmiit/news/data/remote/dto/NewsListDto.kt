package com.egormelnikoff.schedulerutmiit.news.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsListDto(
    @SerialName("maxPage")
    val maxPage: Int,
    @SerialName("items")
    val items: List<NewsShortDto>
)