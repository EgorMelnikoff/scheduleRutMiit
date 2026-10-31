package com.egormelnikoff.schedulerutmiit.search.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InstitutesDto(
    @SerialName("institutes")
    val institutes: List<InstituteDto>
)