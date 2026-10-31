package com.egormelnikoff.schedulerutmiit.latest_release.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestReleaseDto(
    @SerialName("html_url")
    val url: String,
    @SerialName("name")
    val name: String,
    @SerialName("tag_name")
    val tag: String
)