package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.model.Institute
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Subject
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.Dao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import javax.inject.Inject

interface SearchRepos {
    suspend fun fetchInstitutes(): Result<Institutes>
    suspend fun getGroupsByQuery(
        institutes: Institutes,
        query: String
    ): Result<List<Group>>

    suspend fun getPeopleByQuery(query: String): Result<List<Person>>
    suspend fun getSubjectsByCurriculum(id: String): Result<List<Subject>>

    suspend fun getAllSearchQuery(): List<SearchQuery>
    suspend fun saveSearchQuery (searchQuery: SearchQuery)
    suspend fun deleteSearchQuery(queryPrimaryKey: Int)
    suspend fun deleteAllSearchQuery()
}

class SearchReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkHelper: NetworkHelper,
    private val parser: Parser,
    private val dao: Dao
) : SearchRepos {
    override suspend fun getPeopleByQuery(query: String) = parser.getPeopleByQuery(query)

    override suspend fun getSubjectsByCurriculum(id: String) = parser.getListSubjectById(id)

    override suspend fun getGroupsByQuery(
        institutes: Institutes,
        query: String
    ): Result<List<Group>> {
        val groups = getGroups(institutes.institutes)
        val filteredGroups = groups.filter {
            compareValues(it.name, query)
        }
        return Result.Success(filteredGroups)
    }

    override suspend fun fetchInstitutes(): Result<Institutes> =
        networkHelper.callNetwork(
            requestType = "Institutes",
            callApi = {
                miitApi.getInstitutes()
            },
            callParser = null
        )

    override suspend fun saveSearchQuery(searchQuery: SearchQuery) {
        dao.saveSearchQuery(searchQuery)
    }

    override suspend fun getAllSearchQuery(): List<SearchQuery> {
        return dao.getAllSearchQuery()
    }

    override suspend fun deleteAllSearchQuery() {
        dao.deleteAllSearchQuery()
    }

    override suspend fun deleteSearchQuery(queryPrimaryKey: Int) {
       dao.deleteSearchQuery(queryPrimaryKey)
    }

    private fun compareValues(comparableValue: String, query: String): Boolean {
        return comparableValue.lowercase().replace("-", "").contains(query.lowercase().replace("-", ""))
    }

    private fun getGroups(institutes: List<Institute>): List<Group> {
        return institutes.flatMap { institute ->
            institute.courses.flatMap { course ->
                course.specialties.flatMap { specialty ->
                    specialty.groups
                }
            }
        }
    }
}
