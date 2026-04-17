package com.egormelnikoff.schedulerutmiit.ui.view_models.search.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto

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