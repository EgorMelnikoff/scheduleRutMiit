package com.egormelnikoff.schedulerutmiit.news.data.remote.dto

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class NewsShortDto(
    @SerialName("idInformation")
    val id: Long,
    @SerialName("title")
    val title: String,
    @Serializable(with = LocalDateTimeInstantSerializer::class)
    @SerialName("date")
    val date: LocalDateTime,
    @SerialName("thumbnail")
    val picUrl: String,
    @SerialName("secondary")
    val secondary:SecondaryDto
)