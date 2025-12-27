package com.egormelnikoff.schedulerutmiit.ui.state.actions.search

import com.egormelnikoff.schedulerutmiit.app.model.SearchOption
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchViewModel

data class SearchActions(
    val changeQuery: (String) -> Unit,
    val changeSearchOption: (SearchOption) -> Unit,
    val search: () -> Unit,
    val setDefaultState: () -> Unit
) {
    companion object {
        fun searchActions(
            searchViewModel: SearchViewModel
        ): SearchActions = SearchActions(
            changeQuery = { query ->
                searchViewModel.changeSearchParams(query = query)
            },
            changeSearchOption = { searchOption ->
                searchViewModel.changeSearchParams(searchOption = searchOption)
            },
            search = {
                searchViewModel.search()
            },
            setDefaultState = {
                searchViewModel.setDefaultSearchState()
            }
        )
    }
}