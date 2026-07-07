package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Person
import com.egormelnikoff.schedulerutmiit.core.common.result.Result

data class SearchResult(
    val groups: Result<List<Group>>?,
    val people: Result<List<Person>>?
)