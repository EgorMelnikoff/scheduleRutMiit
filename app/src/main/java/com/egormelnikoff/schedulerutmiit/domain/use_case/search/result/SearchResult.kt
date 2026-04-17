package com.egormelnikoff.schedulerutmiit.domain.use_case.search.result

import com.egormelnikoff.schedulerutmiit.data.remote.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result

data class SearchResult(
    val groups: Result<List<GroupDto>>?,
    val people: Result<List<PersonDto>>?
)