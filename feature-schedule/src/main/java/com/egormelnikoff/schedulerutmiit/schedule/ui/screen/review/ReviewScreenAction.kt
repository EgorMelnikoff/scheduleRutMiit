package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review

sealed interface ReviewScreenAction {
    data object OnNavigateFindScheduleClicked : ReviewScreenAction
    data object OnCreateScheduleClicked : ReviewScreenAction
    data object OnNewsCLicked : ReviewScreenAction
    data object OnCurriculumClicked : ReviewScreenAction

    data class OnShowSchedules(val show: Boolean): ReviewScreenAction
    data class OnShowServices(val show: Boolean): ReviewScreenAction
}