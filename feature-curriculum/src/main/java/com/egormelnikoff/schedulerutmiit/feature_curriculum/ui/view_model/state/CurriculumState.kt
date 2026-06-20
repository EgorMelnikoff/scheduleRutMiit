package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError
import com.egormelnikoff.schedulerutmiit.core.network.dto.subjects.SubjectDto

data class CurriculumState(
    val subjectsList: List<SubjectDto> = listOf(),
    val error: TypedError? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)