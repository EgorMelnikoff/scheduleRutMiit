package com.egormelnikoff.schedulerutmiit.latest_release.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestRelease(
    @SerialName("html_url")
    val url: String,
    @SerialName("name")
    val name: String,
    @SerialName("tag_name")
    val tag: String,
    val time: Long = System.currentTimeMillis()
) {
    fun isOutdated(threshold: Long): Boolean {
        return (System.currentTimeMillis() - time) > threshold
    }
}