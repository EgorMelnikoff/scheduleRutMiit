package com.egormelnikoff.schedulerutmiit.data.remote.dto.person

import androidx.annotation.Keep

@Keep
data class PersonDto(
    val name: String,
    val id: Int,
    val position: String
)