package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ReviewUiState(
    val visibleSavedSchedules: Boolean,
    val visibleServices: Boolean,
    val onChangeVisibilitySavedSchedules: (Boolean) -> Unit,
    val onChangeVisibilityServices: (Boolean) -> Unit
) {
    companion object {
        @Composable
        operator fun invoke(): ReviewUiState {
            var visibleSavedSchedules by remember { mutableStateOf(true) }
            var visibleServices by remember { mutableStateOf(true) }

            return ReviewUiState(
                visibleSavedSchedules = visibleSavedSchedules,
                visibleServices = visibleServices,
                onChangeVisibilitySavedSchedules = { newValue ->
                    visibleSavedSchedules = newValue
                },
                onChangeVisibilityServices = { newValue ->
                    visibleServices = newValue
                }
            )
        }
    }
}


