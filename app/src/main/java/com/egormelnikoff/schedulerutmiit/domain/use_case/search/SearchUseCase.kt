package com.egormelnikoff.schedulerutmiit.domain.use_case.search

import com.egormelnikoff.schedulerutmiit.app.enums.SearchType
import com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes.InstituteDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import com.egormelnikoff.schedulerutmiit.domain.repos.SearchRemoteDataSource
import com.egormelnikoff.schedulerutmiit.domain.use_case.search.result.SearchResult
import com.egormelnikoff.schedulerutmiit.ui.view_models.search.state.SearchParams
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource
) {
    suspend operator fun invoke(
        searchParams: SearchParams,
        institutesDto: InstitutesDto?
    ): SearchResult {
        var groups: Result<List<GroupDto>>? = null
        var people: Result<List<PersonDto>>? = null

        if ((searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.GROUPS) && institutesDto != null) {
            groups = Result.Success(getGroupsByQuery(institutesDto, searchParams.query))
        }

        if (searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.PEOPLE) {
            people = searchRemoteDataSource.fetchPeopleByQuery(searchParams.query)
        }

        return SearchResult(
            groups = groups,
            people = people
        )
    }


    fun getGroupsByQuery(
        institutesDto: InstitutesDto,
        query: String
    ): List<GroupDto> {
        val groups = getGroups(institutesDto.institutes)

        return groups.filter {
            compareValues(it.name, query)
        }
    }

    private fun compareValues(comparableValue: String, query: String): Boolean {
        val cleanValue = comparableValue.filter { !it.isWhitespace() }
        val cleanQuery = query.filter { !it.isWhitespace() }

        return cleanValue.contains(cleanQuery, ignoreCase = true)
    }

    private fun getGroups(instituteModels: List<InstituteDto>): List<GroupDto> {
        return instituteModels.flatMap { institute ->
            institute.courses.flatMap { course ->
                course.specialties.flatMap { specialty ->
                    specialty.groups
                }
            }
        }
    }
}