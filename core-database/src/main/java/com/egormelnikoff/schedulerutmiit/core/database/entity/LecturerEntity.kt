package com.egormelnikoff.schedulerutmiit.core.database.entity

import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import kotlinx.serialization.Serializable

@Serializable
data class LecturerEntity(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String = ""
)

