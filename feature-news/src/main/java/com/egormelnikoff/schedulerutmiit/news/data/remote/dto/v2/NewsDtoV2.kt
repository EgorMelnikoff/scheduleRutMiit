package com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsDtoV2(
    @SerialName("idInformation")
    val idInformation: String,
    @SerialName("title")
    val title: String,
    @SerialName("hisdateDisplay")
    val date: String,
    @SerialName("content")
    val content: String
)