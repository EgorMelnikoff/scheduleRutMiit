package com.egormelnikoff.schedulerutmiit.core.network.dto.institutes

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class InstitutesDto(
    @SerialName("institutes")
    val institutes: List<InstituteDto>
)