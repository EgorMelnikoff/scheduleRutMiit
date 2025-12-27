package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ReviewUiState(
    val visibleSavedSchedules: Boolean,
    val onChangeVisibilitySavedSchedules: (Boolean) -> Unit
) {
    companion object {
        @Composable
        fun reviewUiState(): ReviewUiState {
            var visibleSavedSchedules by remember { mutableStateOf(true) }

            return ReviewUiState(
                visibleSavedSchedules = visibleSavedSchedules,
                onChangeVisibilitySavedSchedules = { newValue ->
                    visibleSavedSchedules = newValue
                }
            )
        }
    }
}


