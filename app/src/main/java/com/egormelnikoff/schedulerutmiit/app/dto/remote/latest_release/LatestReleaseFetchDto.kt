package com.egormelnikoff.schedulerutmiit.app.dto.remote.latest_release

import com.google.gson.annotations.SerializedName

data class LatestReleaseFetchDto(
    @SerializedName("html_url")
    val url: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tag_name")
    val tag: String,
    val time: Long = System.currentTimeMillis()
) {
    fun isOutdated(threshold: Long): Boolean {
        return (System.currentTimeMillis() - time) > threshold
    }
}