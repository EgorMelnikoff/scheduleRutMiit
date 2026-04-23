package com.egormelnikoff.schedulerutmiit.core.network.dto.person

import androidx.annotation.Keep

@Keep
data class PersonDto(
    val name: String,
    val id: Int,
    val position: String
)