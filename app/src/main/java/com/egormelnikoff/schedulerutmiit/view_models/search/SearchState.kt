package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.network.model.InstitutesModel
import com.egormelnikoff.schedulerutmiit.app.network.model.PersonModel

@Keep
data class SearchState(
    val history: List<SearchQuery> = listOf(),
    val institutesModel: InstitutesModel? = null,
    val groups: List<Group> = listOf(),
    val people: List<PersonModel> = listOf(),
    val error: String? = null,
    val isEmptyQuery: Boolean = true,
    val isLoading: Boolean = false
)