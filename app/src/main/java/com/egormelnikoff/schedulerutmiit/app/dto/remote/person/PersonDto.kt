package com.egormelnikoff.schedulerutmiit.app.dto.remote.person

import androidx.annotation.Keep

@Keep
data class PersonDto(
    val name: String,
    val id: Int,
    val position: String
)