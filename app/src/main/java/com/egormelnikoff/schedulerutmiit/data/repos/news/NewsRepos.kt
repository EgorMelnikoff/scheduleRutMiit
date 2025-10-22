package com.egormelnikoff.schedulerutmiit.data.repos.news

import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsList
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import javax.inject.Inject

interface NewsRepos {
    fun parseNews(news: News): News
    suspend fun getNewsList(pageNumber: Int): Result<NewsList>
    suspend fun getNewsById(id: Long): Result<News>
}

class NewsReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val miitApiHelper: MiitApiHelper,
    private val parser: Parser
) : NewsRepos {
    override fun parseNews(news: News): News {
        return parser.parseNews(news)
    }

    override suspend fun getNewsList(pageNumber: Int): Result<NewsList> {
        return miitApiHelper.callApiWithExceptions(
            type = "News list"
        ){
            miitApi.getNewsList(
                fromPage = pageNumber.toString(),
                toPage = pageNumber.toString()
            )
        }
    }

    override suspend fun getNewsById(id: Long): Result<News> {
        return miitApiHelper.callApiWithExceptions (
            type = "News by id: $id"
        ){
            miitApi.getNewsById(id)
        }
    }
}