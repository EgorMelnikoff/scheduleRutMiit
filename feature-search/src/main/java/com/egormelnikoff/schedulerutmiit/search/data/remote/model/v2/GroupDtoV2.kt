package com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GroupDtoV2(
    @SerialName("idGroup")
    val idGroup: Int,
    @SerialName("egName")
    val egName: String,
    @SerialName("course")
    val course: Int,
    @SerialName("specName")
    val specName: String
)