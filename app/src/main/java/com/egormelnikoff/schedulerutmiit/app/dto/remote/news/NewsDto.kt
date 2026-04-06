package com.egormelnikoff.schedulerutmiit.app.dto.remote.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NewsDto(
    @SerializedName("idInformation")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("hisdateDisplay")
    val date: String,
    @SerializedName("content")
    val content: String
)