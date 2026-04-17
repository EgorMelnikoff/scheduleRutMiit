package com.egormelnikoff.schedulerutmiit.data.remote.dto.news

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NewsListDto(
    @SerialName("maxPage")
    val maxPage: Int,
    @SerialName("items")
    val items: List<NewsShortDto>
)