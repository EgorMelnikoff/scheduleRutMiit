package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserHelper.PEOPLE
import javax.inject.Inject

interface SearchRepos {
    suspend fun getInstitutes(): Result<Institutes>
    suspend fun getPeople(query: String): Result<List<Person>>
}

class SearchReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val miitApiHelper: MiitApiHelper,
    private val parser: Parser
) : SearchRepos {
    override suspend fun getPeople(query: String): Result<List<Person>> {
        return try {
            parser.parsePeople("$PEOPLE$query")
        } catch (e: Exception) {
            Result.Error(Error.UnexpectedError(e))
        }
    }

    override suspend fun getInstitutes(): Result<Institutes> {
        return miitApiHelper.callApiWithExceptions (
            type = "Institutes"
        ) {
            miitApi.getInstitutes()
        }
    }
}
