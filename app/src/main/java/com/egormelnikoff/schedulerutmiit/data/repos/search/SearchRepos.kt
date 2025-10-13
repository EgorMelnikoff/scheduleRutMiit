package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.PEOPLE
import com.egormelnikoff.schedulerutmiit.model.Institutes
import com.egormelnikoff.schedulerutmiit.model.Person
import javax.inject.Inject

interface SearchRepos {
    suspend fun getInstitutes(): Result<Institutes>
    suspend fun getPeople(query: String): Result<List<Person>>
}

class SearchReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val parser: Parser
) : SearchRepos {
    override suspend fun getPeople(query: String): Result<List<Person>> {
        return try {
            parser.parsePeople("$PEOPLE$query")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getInstitutes(): Result<Institutes> {
        return try {
            val institutes = miitApi.getInstitutes()
            if (institutes.isSuccessful && institutes.body() != null) {
                Result.Success(institutes.body()!!)
            } else {
                Result.Error(NoSuchElementException())
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
