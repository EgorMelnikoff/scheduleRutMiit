package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person

internal data class SearchResult(
    val groups: List<Group>?,
    val people: List<Person>?
)