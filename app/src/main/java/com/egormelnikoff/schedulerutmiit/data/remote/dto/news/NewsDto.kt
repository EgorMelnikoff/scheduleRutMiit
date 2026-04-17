package com.egormelnikoff.schedulerutmiit.data.remote.dto.news

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
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