package com.egormelnikoff.schedulerutmiit.ui.state.actions.search

import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchOption
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel

data class SearchActions(
    val onSetDefaultSearchState: () -> Unit,
    val onSearchSchedule: (Pair<String, SearchOption>) -> Unit, //Query, SearchOption
) {
    companion object {
        fun getSearchActions(
            searchViewModel: SearchViewModel,
            reviewUiState: ReviewUiState,
        ) = SearchActions(
            onSetDefaultSearchState = {
                searchViewModel.setDefaultSearchState()
                reviewUiState.onChangeQuery("")
                reviewUiState.onSelectSearchOption(SearchOption.ALL)
            },
            onSearchSchedule = { value ->
                searchViewModel.search(
                    query = value.first,
                    selectedSearchOption = value.second
                )
            }
        )
    }
}