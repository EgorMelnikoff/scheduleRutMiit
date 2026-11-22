package com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsCategory(
    @SerialName("idInformationCategory")
    val id: Int,
    @SerialName("icName")
    val name: String
)