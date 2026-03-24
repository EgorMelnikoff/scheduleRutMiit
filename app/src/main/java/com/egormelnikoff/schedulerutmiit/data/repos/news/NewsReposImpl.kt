package com.egormelnikoff.schedulerutmiit.data.repos.news

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsShort
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.local.parser.NewsParser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkHelper: NetworkHelper,
    private val resourcesManager: ResourcesManager,
    private val newsParser: NewsParser
) : NewsRepos {
    override suspend fun parseNews(news: News) = newsParser(news)

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


    override suspend fun getNewsById(id: Long) = networkHelper.callNetwork(
        requestType = "News",
        requestParams = "News id: $id",
        callApi = {
            miitApi.getNewsById(id)
        },
        callJsoup = null
    )
}