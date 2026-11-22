package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model

internal data class Lecturer(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String
)