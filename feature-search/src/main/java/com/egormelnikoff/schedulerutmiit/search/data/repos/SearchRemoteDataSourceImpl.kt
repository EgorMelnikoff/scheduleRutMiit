package com.egormelnikoff.schedulerutmiit.search.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkHelper
import com.egormelnikoff.schedulerutmiit.search.data.parser.SearchParser
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRemoteDataSource
import org.jsoup.Jsoup
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val searchParser: SearchParser,
    private val networkHelper: NetworkHelper
) : SearchRemoteDataSource {
    override suspend fun fetchInstitutes() =
        networkHelper.callNetwork(
            requestType = "Institutes",
            callApi = {
                miitApi.getInstitutes()
            },
            callJsoup = null
        )

    override suspend fun fetchPeopleByQuery(query: String): Result<List<PersonDto>> {
        networkHelper.callNetwork(
            requestType = "Person",
            requestParams = "Query: $query",
            callJsoup = {
                Jsoup.connect(Endpoints.peopleUrl(query)).get()
            },
            callApi = null
        ).let {
            return when (it) {
                is Result.Error -> it

                is Result.Success -> {
                    Result.Success(
                        searchParser.parsePeople(
                            it.data
                        )
                    )
                }
            }
        }
    }
}