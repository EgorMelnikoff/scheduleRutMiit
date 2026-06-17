package com.egormelnikoff.schedulerutmiit.core.common.domain

data class ScreenState(
    val isError: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)