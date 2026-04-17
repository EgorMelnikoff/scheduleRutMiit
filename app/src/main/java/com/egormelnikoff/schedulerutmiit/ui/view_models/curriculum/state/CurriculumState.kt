package com.egormelnikoff.schedulerutmiit.ui.view_models.curriculum.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.remote.dto.subjects.SubjectDto

@Keep
data class CurriculumState(
    val subjectsList: List<SubjectDto> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)