package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.model

internal data class SubjectDto(
    val title: String,
    val teachers: Set<String>
)