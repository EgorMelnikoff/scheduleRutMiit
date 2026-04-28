package com.egormelnikoff.schedulerutmiit.core.network.dto.news

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsDto(
    @SerialName("idInformation")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("hisdateDisplay")
    val date: String,
    @SerialName("content")
    val content: String
)