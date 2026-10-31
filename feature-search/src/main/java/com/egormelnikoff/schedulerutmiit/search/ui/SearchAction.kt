package com.egormelnikoff.schedulerutmiit.search.ui

import com.egormelnikoff.schedulerutmiit.search.domain.model.RefreshArgs
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType

internal sealed interface SearchAction {
    data class OnChangeParams(
        val query: String? = null,
        val searchType: SearchType? = null
    ) : SearchAction

    data class OnSaveQuery(val searchQuery: SearchQuery) : SearchAction
    data class OnDeleteQuery(val queryId: Long) : SearchAction
    data class OnNavigateSchedule(val refreshArgs: RefreshArgs) : SearchAction
    data object OnClearParams : SearchAction
}