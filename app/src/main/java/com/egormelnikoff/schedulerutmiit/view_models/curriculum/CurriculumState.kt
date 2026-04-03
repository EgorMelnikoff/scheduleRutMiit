package com.egormelnikoff.schedulerutmiit.view_models.curriculum

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.network.model.SubjectModel

@Keep
data class CurriculumState(
    val subjectsList: List<SubjectModel> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)