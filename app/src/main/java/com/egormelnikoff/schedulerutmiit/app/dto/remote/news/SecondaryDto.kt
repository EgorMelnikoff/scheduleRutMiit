package com.egormelnikoff.schedulerutmiit.app.dto.remote.news

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SecondaryDto(
    @SerialName("text")
    val text: String
)