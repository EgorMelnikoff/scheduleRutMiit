package com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PersonDtoV2(
    @SerialName("fio")
    val fio: String,
    @SerialName("idE")
    val idE: Int,
    @SerialName("photoUrl")
    val photoUrl: String?,
    @SerialName("postNames")
    val postNames: List<PostNameDtoV2>
)