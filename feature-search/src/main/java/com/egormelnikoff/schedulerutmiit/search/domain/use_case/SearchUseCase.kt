package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Person
import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRemoteDataSource
import com.egormelnikoff.schedulerutmiit.search.ui.view_model.state.SearchParams
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource
) {
    private var cachedGroups: List<Group>? = null

    suspend operator fun invoke(
        searchParams: SearchParams
    ): SearchResult {
        if (cachedGroups == null) {
            searchRemoteDataSource.fetchAllGroups().let { groups ->
                if (groups is Result.Success) {
                    cachedGroups = groups.data
                }
            }
        }

        var groups: Result<List<Group>>? = null
        var people: Result<List<Person>>? = null

        if ((searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.GROUPS) && cachedGroups != null) {
            groups = Result.Success(
                cachedGroups!!.filterByQuery(searchParams.query)
            )
        }

        if (searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.PEOPLE) {
            people = searchRemoteDataSource.fetchPeopleByQuery(searchParams.query)
        }

        return SearchResult(
            groups = groups,
            people = people
        )
    }

    fun List<Group>.filterByQuery(
        query: String
    ) = this.filter { group ->
        val cleanValue = group.name.filter { !it.isWhitespace() }
        val cleanQuery = query.filter { !it.isWhitespace() }

        cleanValue.contains(cleanQuery, ignoreCase = true)
    }
}