package com.egormelnikoff.schedulerutmiit.search.ui

import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType

internal data class SearchState(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL,
    val isLoading: Boolean = false,
    val isEmptyQuery: Boolean = true,
    val groups: List<Group> = listOf(),
    val people: List<Person> = listOf(),
)