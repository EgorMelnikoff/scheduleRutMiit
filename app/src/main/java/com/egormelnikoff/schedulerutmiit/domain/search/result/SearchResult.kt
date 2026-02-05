package com.egormelnikoff.schedulerutmiit.domain.search.result

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Result

data class SearchResult(
    val groups: Result<List<Group>>?,
    val people: Result<List<Person>>?
)