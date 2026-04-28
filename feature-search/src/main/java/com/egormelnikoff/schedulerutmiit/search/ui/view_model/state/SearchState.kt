package com.egormelnikoff.schedulerutmiit.search.ui.view_model.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.database.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.core.network.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.GroupDto

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