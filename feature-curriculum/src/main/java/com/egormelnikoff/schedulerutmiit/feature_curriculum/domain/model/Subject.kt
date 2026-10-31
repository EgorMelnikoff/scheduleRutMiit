package com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model

internal data class Subject(
    val title: String,
    val teachers: Set<String>
)