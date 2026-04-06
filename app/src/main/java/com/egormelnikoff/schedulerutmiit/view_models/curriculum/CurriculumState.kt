package com.egormelnikoff.schedulerutmiit.view_models.curriculum

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.subjects.SubjectDto

@Keep
data class CurriculumState(
    val subjectsList: List<SubjectDto> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)