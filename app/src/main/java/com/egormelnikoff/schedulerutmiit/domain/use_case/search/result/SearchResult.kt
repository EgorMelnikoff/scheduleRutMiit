package com.egormelnikoff.schedulerutmiit.domain.use_case.search.result

import com.egormelnikoff.schedulerutmiit.core.common.dto.GroupDto
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.person.PersonDto

data class SearchResult(
    val groups: Result<List<GroupDto>>?,
    val people: Result<List<PersonDto>>?
)