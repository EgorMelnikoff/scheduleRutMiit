package com.egormelnikoff.schedulerutmiit.app.dto.remote.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NewsListDto(
    @SerializedName("maxPage")
    val maxPage: Int,
    @SerializedName("items")
    val items: List<NewsShortDto>
)