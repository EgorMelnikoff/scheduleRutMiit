package com.egormelnikoff.schedulerutmiit.data.repos.news.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsShort
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsRepos
import com.egormelnikoff.schedulerutmiit.data.repos.news.PagingNewsSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkHelper: NetworkHelper,
    private val resourcesManager: ResourcesManager,
    private val parser: Parser
) : NewsRepos {
    override fun parseNews(news: News) = parser.parseNews(news)

    override fun getNewsListFlow(): Flow<PagingData<NewsShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingNewsSource(
                    miitApi = miitApi,
                    networkHelper = networkHelper,
                    resourcesManager = resourcesManager
                )
            }
        ).flow
    }


    override suspend fun getNewsById(id: Long): Result<News> {
        return networkHelper.callNetwork(
            requestType = "News",
            requestParams = "News id: $id",
            callApi = {
                miitApi.getNewsById(id)
            },
            callParser = null
        )
    }
}