package com.egormelnikoff.schedulerutmiit.data.repos.news

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.Api
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.model.NewsList
import javax.inject.Inject

interface NewsRepos {
    fun parseNews(news: News): News
    suspend fun getNewsList(page: String): Result<NewsList>
    suspend fun getNewsById(id: Long): Result<News>
}

class NewsReposImpl @Inject constructor(
    private val api: Api,
    private val parser: Parser
) : NewsRepos {
    override fun parseNews(news: News): News {
        return parser.parseNews(news)
    }

    override suspend fun getNewsList(page: String): Result<NewsList> {
        return try {
            val newsList = api.getNewsList(fromPage = page, toPage = page)
            if (newsList.isSuccessful && newsList.body() != null) {
                Result.Success(newsList.body()!!)
            } else {
                Result.Error(NoSuchElementException())
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNewsById(id: Long): Result<News> {
        return try {
            val news = api.getNewsById(id)
            if (news.isSuccessful && news.body() != null) {
                Result.Success(news.body()!!)
            } else {
                Result.Error(NoSuchElementException())
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}