package com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InstitutesDto(
    @SerializedName("institutes")
    val institutes: List<InstituteDto>
)