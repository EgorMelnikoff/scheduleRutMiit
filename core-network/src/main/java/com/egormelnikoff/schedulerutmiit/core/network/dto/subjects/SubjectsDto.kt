package com.egormelnikoff.schedulerutmiit.core.network.dto.subjects

data class SubjectDto(
    val title: String,
    val teachers: Set<String>
)