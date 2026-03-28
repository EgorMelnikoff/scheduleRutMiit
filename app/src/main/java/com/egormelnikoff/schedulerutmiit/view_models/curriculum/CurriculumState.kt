package com.egormelnikoff.schedulerutmiit.view_models.curriculum

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.network.model.Subject

@Keep
data class CurriculumState(
    val subjectsList: List<Subject> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)