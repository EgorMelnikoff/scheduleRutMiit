package com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsShortDtoV2(
    @SerialName("idInformation")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("hisdateDisplay")
    val date: String?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("source")
    val source: String?
)