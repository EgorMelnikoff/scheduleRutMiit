package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui

internal sealed interface CurriculumAction {
    data class OnChangeQuery(
        val query: String
    ) : CurriculumAction

    data object OnClearState : CurriculumAction

    data object OnShowAdviceDialog : CurriculumAction

    data object OnHideAdviceDialog : CurriculumAction
}