package com.egormelnikoff.schedulerutmiit.domain.search

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.SearchType
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.repos.search.SearchRepos
import com.egormelnikoff.schedulerutmiit.domain.search.result.SearchResult
import javax.inject.Inject


class SearchUseCase @Inject constructor(
    private val searchRepos: SearchRepos
) {
    suspend operator fun invoke(
        query: String,
        searchType: SearchType,
        institutes: Institutes?
    ): SearchResult {
        var groups: Result<List<Group>>? = null
        var people: Result<List<Person>>? = null

        if ((searchType == SearchType.ALL || searchType == SearchType.GROUPS) && institutes != null) {
            groups = searchRepos.getGroupsByQuery(institutes, query)
        }

        if (searchType == SearchType.ALL || searchType == SearchType.PEOPLE) {
            people = searchRepos.getPeopleByQuery(query)
        }

        return SearchResult(
            groups = groups,
            people = people
        )
    }
}