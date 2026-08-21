package com.egormelnikoff.schedulerutmiit.search.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.dto.institutes.InstituteDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkExecutor
import com.egormelnikoff.schedulerutmiit.core.network.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.search.data.parser.SearchParser
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRemoteDataSource
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkExecutor: NetworkExecutor
) : SearchRemoteDataSource {
    override suspend fun fetchAllGroups() = networkExecutor.callApi {
        miitApi.getInstitutes()
    }.let {
        return@let when (it) {
            is Result.Error -> it
            is Result.Success -> Result.Success(it.data.institutes.getGroups())
        }
    }


    override suspend fun fetchPeopleByQuery(query: String) = networkExecutor.callHtml(
        url = Endpoints.peopleUrl(query)
    ).let {
        return@let when (it) {
            is Result.Error -> it

            is Result.Success -> {
                Result.Success(
                    SearchParser.parsePeople(it.data).map { p ->
                        p.toDomain()
                    }
                )
            }
        }
    }

    private fun List<InstituteDto>.getGroups() = this.flatMap { institute ->
        institute.courses.flatMap { course ->
            course.specialties.flatMap { specialty ->
                specialty.groups.map { it.toDomain() }
            }
        }
    }
}