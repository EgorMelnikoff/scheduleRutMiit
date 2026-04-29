package com.egormelnikoff.schedulerutmiit.news.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkHelper
import com.egormelnikoff.schedulerutmiit.news.data.parser.NewsParser
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRemoteDataSource
import javax.inject.Inject

class NewsRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val newsParser: NewsParser,
    private val networkHelper: NetworkHelper
) : NewsRemoteDataSource {

    override suspend fun getNewsList(pageSize: Int, page: Int) = networkHelper.callApi(
        requestType = "News list",
        requestParams = "From page: $page; To page: $page"
    ) {
        miitApi.getNewsList(pageSize, page, page)
    }

    override suspend fun getNewsById(id: Long): Result<NewsParsedDto> {
        networkHelper.callApi(
            requestType = "News",
            requestParams = "News id: $id"
        ) {
            miitApi.getNewsById(id)
        }.let {
            return when (it) {
                is Result.Error -> it
                is Result.Success -> Result.Success(
                    newsParser(it.data)
                )
            }
        }
    }
}