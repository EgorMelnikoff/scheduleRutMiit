package com.egormelnikoff.schedulerutmiit.core.network.dto.latest_release

import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestReleaseFetchDto(
    @SerialName("html_url")
    val url: String,
    @SerialName("name")
    val name: String,
    @SerialName("tag_name")
    val tag: String
)