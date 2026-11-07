package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule

data class ScreenState(
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = true
)