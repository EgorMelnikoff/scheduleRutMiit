package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.GroupDto

data class SearchResult(
    val groups: Result<List<GroupDto>>?,
    val people: Result<List<PersonDto>>?
)