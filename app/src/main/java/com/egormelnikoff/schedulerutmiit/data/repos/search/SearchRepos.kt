package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserHelper.Companion.PEOPLE
import javax.inject.Inject

interface SearchRepos {
    suspend fun getInstitutes(): Result<Institutes>
    suspend fun getPeople(query: String): Result<List<Person>>
}

class SearchReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val apiHelper: ApiHelper,
    private val parser: Parser
) : SearchRepos {
    override suspend fun getPeople(query: String) = parser.parsePeople("$PEOPLE$query")

    override suspend fun getInstitutes() = apiHelper.callApiWithExceptions(
        fetchDataType = "Institutes"
    ) {
        miitApi.getInstitutes()
    }
}
