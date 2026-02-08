package com.egormelnikoff.schedulerutmiit.domain.search

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.SearchType
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.domain.search.result.SearchResult
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchParams
import javax.inject.Inject


class SearchUseCase @Inject constructor(
    private val searchRepos: SearchRepos
) {
    suspend operator fun invoke(
        searchParams: SearchParams,
        institutes: Institutes?
    ): SearchResult {
        var groups: Result<List<Group>>? = null
        var people: Result<List<Person>>? = null

        if ((searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.GROUPS) && institutes != null) {
            groups = searchRepos.getGroupsByQuery(institutes, searchParams.query)
        }

        if (searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.PEOPLE) {
            people = searchRepos.getPeopleByQuery(searchParams.query)
        }

        return SearchResult(
            groups = groups,
            people = people
        )
    }
}