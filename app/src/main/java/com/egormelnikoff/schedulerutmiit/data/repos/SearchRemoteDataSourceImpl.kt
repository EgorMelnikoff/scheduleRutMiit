package com.egormelnikoff.schedulerutmiit.data.repos

import com.egormelnikoff.schedulerutmiit.data.local.parser.SearchParser
import com.egormelnikoff.schedulerutmiit.data.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.person.PersonDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.Endpoints
import com.egormelnikoff.schedulerutmiit.data.remote.network.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import com.egormelnikoff.schedulerutmiit.domain.repos.SearchRemoteDataSource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val searchParser: SearchParser,
    private val networkHelper: NetworkHelper
) : SearchRemoteDataSource {
    override suspend fun fetchInstitutes(): Result<InstitutesDto> =
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

    override suspend fun fetchSubjects(id: String, page: Int): Result<Document> {
        return networkHelper.callNetwork(
            requestType = "Subjects",
            requestParams = "Id: $id; Page: $page",
            callJsoup = {
                Jsoup.connect(Endpoints.curriculumProfessorsUrl(id, page)).get()
            },
            callApi = null
        )
    }

}