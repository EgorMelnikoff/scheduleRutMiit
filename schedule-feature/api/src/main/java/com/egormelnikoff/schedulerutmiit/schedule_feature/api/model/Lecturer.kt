package com.egormelnikoff.schedulerutmiit.schedule_feature.api.model

data class Lecturer(
    val id: Int = -1,
    val shortFio: String,
    val fullFio: String,
    val hint: String
)