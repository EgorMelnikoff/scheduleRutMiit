package com.egormelnikoff.schedulerutmiit.core.network.dto.news

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SecondaryDto(
    @SerialName("text")
    val text: String
)