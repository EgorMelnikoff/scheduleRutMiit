package com.egormelnikoff.schedulerutmiit.core.common.dto.person

import androidx.annotation.Keep

@Keep
data class PersonDto(
    val name: String,
    val id: Int,
    val position: String
)