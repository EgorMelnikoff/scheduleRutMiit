package com.egormelnikoff.schedulerutmiit.data.repos.news

import androidx.paging.PagingData
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsContent
import com.egormelnikoff.schedulerutmiit.app.model.NewsShort
import com.egormelnikoff.schedulerutmiit.data.Result
import kotlinx.coroutines.flow.Flow

interface NewsRepos {
    suspend fun parseNews(news: News): NewsContent
    fun getNewsListFlow(): Flow<PagingData<NewsShort>>
    suspend fun getNewsById(id: Long): Result<News>
}