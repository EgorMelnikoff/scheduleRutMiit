package com.egormelnikoff.schedulerutmiit.news.data.remote

import com.egormelnikoff.schedulerutmiit.news.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsList
import javax.inject.Inject

internal class NewsRemoteDataSource @Inject constructor(
    private val newsApi: NewsApi,
) {
    suspend fun getNewsList(pageSize: Int, page: Int): NewsList =
        newsApi.getNewsList(pageSize, page, page, 2).toDomain()


    suspend fun getNewsById(id: Long): News =
        newsApi.getNewsById(id).toDomain()
}