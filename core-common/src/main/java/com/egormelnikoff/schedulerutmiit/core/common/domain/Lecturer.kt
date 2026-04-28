package com.egormelnikoff.schedulerutmiit.core.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class Lecturer(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String = ""
)