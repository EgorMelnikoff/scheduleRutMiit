package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRepos
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SearchUseCase @Inject constructor(
    private val searchRepos: SearchRepos
) {
    private var cachedGroups: List<Group>? = null

    suspend operator fun invoke(
        searchParams: Pair<String, SearchType>
    ): SearchResult = withContext(Dispatchers.IO) {
        if (cachedGroups == null) {
            searchRepos.fetchAllGroups().let { groups ->
                cachedGroups = groups
            }
        }

        var groups: List<Group>? = null
        var people: List<Person>? = null

        if ((searchParams.second == SearchType.ALL || searchParams.second == SearchType.GROUPS) && cachedGroups != null) {
            groups = cachedGroups!!.filterByQuery(searchParams.first)

        }

        if (searchParams.second == SearchType.ALL || searchParams.second == SearchType.PEOPLE) {
            people = searchRepos.fetchPeopleByQuery(searchParams.first)
        }

        return@withContext SearchResult(
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