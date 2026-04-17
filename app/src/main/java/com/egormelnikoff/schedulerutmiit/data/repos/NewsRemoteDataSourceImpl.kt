package com.egormelnikoff.schedulerutmiit.data.repos

import com.egormelnikoff.schedulerutmiit.data.local.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.data.local.parser.NewsParser
import com.egormelnikoff.schedulerutmiit.data.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.remote.network.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import com.egormelnikoff.schedulerutmiit.domain.repos.NewsRemoteDataSource
import javax.inject.Inject

class NewsRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val newsParser: NewsParser,
    private val networkHelper: NetworkHelper
) : NewsRemoteDataSource {
    override suspend fun getNewsList(pageSize: Int, page: Int) = networkHelper.callNetwork(
        requestType = "News list",
        requestParams = "From page: $page; To page: $page",
        callApi = {
            miitApi.getNewsList(pageSize, page, page)
        },
        callJsoup = null
    )

    override suspend fun getNewsById(id: Long): Result<NewsParsedDto> {
        networkHelper.callNetwork(
            requestType = "News",
            requestParams = "News id: $id",
            callApi = {
                miitApi.getNewsById(id)
            },
            callJsoup = null
        ).let {
            return when (it) {
                is Result.Error -> it
                is Result.Success -> Result.Success(
                    newsParser(it.data)
                )
            }
        }
    }
}