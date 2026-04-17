package com.egormelnikoff.schedulerutmiit.app.dto.remote.latest_release

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LatestReleaseFetchDto(
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