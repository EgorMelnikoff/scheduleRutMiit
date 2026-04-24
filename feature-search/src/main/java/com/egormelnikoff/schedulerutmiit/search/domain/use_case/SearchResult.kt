package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.GroupDto
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.dto.person.PersonDto

data class SearchResult(
    val groups: Result<List<GroupDto>>?,
    val people: Result<List<PersonDto>>?
)