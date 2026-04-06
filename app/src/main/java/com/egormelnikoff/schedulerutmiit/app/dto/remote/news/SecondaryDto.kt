package com.egormelnikoff.schedulerutmiit.app.dto.remote.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SecondaryDto(
    @SerializedName("text")
    val text: String
)