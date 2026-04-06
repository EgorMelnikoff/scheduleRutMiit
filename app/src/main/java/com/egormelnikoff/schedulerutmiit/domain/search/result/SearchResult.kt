package com.egormelnikoff.schedulerutmiit.domain.search.result

import com.egormelnikoff.schedulerutmiit.app.dto.remote.person.PersonDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

data class SearchResult(
    val groups: Result<List<GroupDto>>?,
    val people: Result<List<PersonDto>>?
)