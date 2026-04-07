package com.egormelnikoff.schedulerutmiit.view_models.search.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.dto.remote.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.person.PersonDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto

@Keep
data class SearchState(
    val history: List<SearchQuery> = listOf(),
    val institutesDto: InstitutesDto? = null,
    val groups: List<GroupDto> = listOf(),
    val people: List<PersonDto> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)