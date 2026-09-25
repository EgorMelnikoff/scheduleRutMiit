package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review

sealed interface ReviewScreenAction {
    data object OnNavigateFindScheduleClicked : ReviewScreenAction
    data object OnCreateScheduleClicked : ReviewScreenAction
    data object OnNewsCLicked : ReviewScreenAction
    data object OnCurriculumClicked : ReviewScreenAction
}