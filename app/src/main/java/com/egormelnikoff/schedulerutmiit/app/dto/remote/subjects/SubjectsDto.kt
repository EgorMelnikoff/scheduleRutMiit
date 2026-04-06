package com.egormelnikoff.schedulerutmiit.app.dto.remote.subjects

import androidx.annotation.Keep

@Keep
data class SubjectDto(
    val title: String,
    val teachers: Set<String>
)