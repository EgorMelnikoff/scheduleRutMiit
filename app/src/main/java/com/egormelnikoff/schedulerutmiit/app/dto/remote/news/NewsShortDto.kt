package com.egormelnikoff.schedulerutmiit.app.dto.remote.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@Keep
data class NewsShortDto(
    @SerializedName("idInformation")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("date")
    val date: LocalDateTime,
    @SerializedName("thumbnail")
    val picUrl: String,
    @SerializedName("secondary")
    val secondary: SecondaryDto
)