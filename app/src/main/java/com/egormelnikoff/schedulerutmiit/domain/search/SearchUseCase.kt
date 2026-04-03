package com.egormelnikoff.schedulerutmiit.domain.search

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums.SearchType
import com.egormelnikoff.schedulerutmiit.app.network.model.InstituteModel
import com.egormelnikoff.schedulerutmiit.app.network.model.InstitutesModel
import com.egormelnikoff.schedulerutmiit.app.network.model.PersonModel
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.datasource.remote.search.SearchRemoteDataSource
import com.egormelnikoff.schedulerutmiit.domain.search.result.SearchResult
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchParams
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource
) {
    suspend operator fun invoke(
        searchParams: SearchParams,
        institutesModel: InstitutesModel?
    ): SearchResult {
        var groups: Result<List<Group>>? = null
        var people: Result<List<PersonModel>>? = null

        if ((searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.GROUPS) && institutesModel != null) {
            groups = Result.Success(getGroupsByQuery(institutesModel, searchParams.query))
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
        institutesModel: InstitutesModel,
        query: String
    ): List<Group> {
        val groups = getGroups(institutesModel.instituteModels)

        return groups.filter {
            compareValues(it.name, query)
        }
    }

    private fun compareValues(comparableValue: String, query: String): Boolean {
        val cleanValue = comparableValue.filter { !it.isWhitespace() }
        val cleanQuery = query.filter { !it.isWhitespace() }

        return cleanValue.contains(cleanQuery, ignoreCase = true)
    }

    private fun getGroups(instituteModels: List<InstituteModel>): List<Group> {
        return instituteModels.flatMap { institute ->
            institute.courses.flatMap { course ->
                course.specialties.flatMap { specialty ->
                    specialty.groups
                }
            }
        }
    }
}