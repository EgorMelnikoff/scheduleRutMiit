package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui

import com.egormelnikoff.schedulerutmiit.core.common.error.AppError
import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model.Subject

internal data class CurriculumState(
    val showAdviceDialog: Boolean = false,
    val searchQuery: String = "",
    val subjectsList: List<Subject> = listOf(),
    val error: AppError? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
) {
    fun setErrorState(e: Throwable) = copy(
        error = e.toAppError(),
        isEmptyQuery = false,
        isLoading = false
    )

    fun setDefaultState() = copy(
        searchQuery = "",
        subjectsList = listOf(),
        isEmptyQuery = true,
        isLoading = false,
        error = null
    )

    fun setContent(result: List<Subject>) = copy(
        subjectsList = result,
        error = null,
        isEmptyQuery = false,
        isLoading = false
    )
}