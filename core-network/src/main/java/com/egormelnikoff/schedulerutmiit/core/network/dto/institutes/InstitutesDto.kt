package com.egormelnikoff.schedulerutmiit.core.network.dto.institutes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstitutesDto(
    @SerialName("institutes")
    val institutes: List<InstituteDto>
)