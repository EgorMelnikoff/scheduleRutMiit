package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person

@Keep
data class SearchState(
    val history: List<SearchQuery> = listOf(),
    val institutes: Institutes? = null,
    val groups: List<Group> = listOf(),
    val people: List<Person> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)