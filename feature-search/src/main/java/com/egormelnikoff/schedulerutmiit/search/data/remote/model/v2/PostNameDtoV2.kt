package com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PostNameDtoV2(
    @SerialName("post_name")
    val postName: String
)