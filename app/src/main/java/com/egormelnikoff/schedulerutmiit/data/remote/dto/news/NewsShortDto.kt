package com.egormelnikoff.schedulerutmiit.data.remote.dto.news

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.local.serializers.LocalDateTimeInstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Keep
@Serializable
data class NewsShortDto(
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
    val secondary: SecondaryDto
)