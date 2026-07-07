package com.egormelnikoff.schedulerutmiit.search.ui.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Person
import com.egormelnikoff.schedulerutmiit.core.common.domain.SearchQuery
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError

data class SearchState(
    val groups: List<Group> = listOf(),
    val people: List<Person> = listOf(),
    val typedError: TypedError? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)