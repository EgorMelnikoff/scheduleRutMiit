package com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsListDtoV2(
    @SerialName("size")
    val size: Int,
    @SerialName("articles")
    val articles: List<NewsShortDtoV2>
)