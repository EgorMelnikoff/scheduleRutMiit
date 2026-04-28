package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.view_model.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.network.dto.subjects.SubjectDto

@Keep
data class CurriculumState(
    val subjectsList: List<SubjectDto> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)