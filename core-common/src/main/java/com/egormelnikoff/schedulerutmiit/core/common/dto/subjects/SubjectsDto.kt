package com.egormelnikoff.schedulerutmiit.core.common.dto.subjects

import androidx.annotation.Keep

@Keep
data class SubjectDto(
    val title: String,
    val teachers: Set<String>
)