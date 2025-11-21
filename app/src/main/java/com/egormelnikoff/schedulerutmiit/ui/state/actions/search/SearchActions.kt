package com.egormelnikoff.schedulerutmiit.ui.state.actions.search

import com.egormelnikoff.schedulerutmiit.ui.dialogs.SearchOption
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel

data class SearchActions(
    val onSetDefaultSearchState: () -> Unit,
    val onSearchSchedule: () -> Unit,
    val onChangeQuery: (String) -> Unit,
    val onSelectSearchOption: (SearchOption) -> Unit,
) {
    companion object {
        fun getSearchActions(
            searchViewModel: SearchViewModel
        ) = SearchActions(
            onSetDefaultSearchState = {
                searchViewModel.setDefaultSearchState()
            },
            onSearchSchedule = {
                searchViewModel.search()
            },
            onChangeQuery = { newValue ->
                searchViewModel.changeSearchParams(query = newValue)
            },
            onSelectSearchOption = { newValue ->
                searchViewModel.changeSearchParams(searchOption = newValue)
            },
        )
    }
}