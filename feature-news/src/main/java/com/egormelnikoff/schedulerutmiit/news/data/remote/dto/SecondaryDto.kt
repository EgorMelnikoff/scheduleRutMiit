package com.egormelnikoff.schedulerutmiit.news.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SecondaryDto(
    @SerialName("text")
    val text: String
)