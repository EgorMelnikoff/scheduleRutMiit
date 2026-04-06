package com.egormelnikoff.schedulerutmiit.datasource.remote.news

import com.egormelnikoff.schedulerutmiit.app.network.NetworkHelper
import com.egormelnikoff.schedulerutmiit.app.dto.local.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.datasource.local.parser.NewsParser
import com.egormelnikoff.schedulerutmiit.datasource.remote.api.MiitApi
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