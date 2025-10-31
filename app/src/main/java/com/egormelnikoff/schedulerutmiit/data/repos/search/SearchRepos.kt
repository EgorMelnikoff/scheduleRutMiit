package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserHelper.Companion.PEOPLE
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
    override suspend fun getPeople(query: String) = parser.parsePeople("$PEOPLE$query")

    override suspend fun getInstitutes() = miitApiHelper.callApiWithExceptions(
        fetchDataType = "Institutes",
        message = ""
    ) {
        miitApi.getInstitutes()
    }
}
