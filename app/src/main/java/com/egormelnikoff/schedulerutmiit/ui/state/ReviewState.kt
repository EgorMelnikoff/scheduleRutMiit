package com.egormelnikoff.schedulerutmiit.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchOption

data class ReviewState(
    val visibleSavedSchedules: Boolean,
    val onChangeVisibilitySavedSchedules: (Boolean) -> Unit,

    val visibleHiddenEvents: Boolean,
    val onChangeVisibilityHiddenEvents: (Boolean) -> Unit,

    val selectedSearchOption: SearchOption,
    val onSelectSearchOption: (SearchOption) -> Unit,

    val searchQuery: String,
    val onChangeQuery: (String) -> Unit
)

@Composable
fun rememberReviewState(): ReviewState {
    var visibleSavedSchedules by remember { mutableStateOf(true) }
    var visibleHiddenEvents by remember { mutableStateOf(false) }

    var selectedSearchOption by remember { mutableStateOf(SearchOption.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    return ReviewState(
        visibleSavedSchedules = visibleSavedSchedules,
        onChangeVisibilitySavedSchedules = { newValue ->
            visibleSavedSchedules = newValue
        },
        visibleHiddenEvents = visibleHiddenEvents,
        onChangeVisibilityHiddenEvents = { newValue ->
            visibleHiddenEvents = newValue
        },
        selectedSearchOption = selectedSearchOption,
        onSelectSearchOption = { newValue ->
            selectedSearchOption = newValue
        },
        searchQuery = searchQuery,
        onChangeQuery = { newValue ->
            searchQuery = newValue
        }
    )
}
