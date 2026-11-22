package com.egormelnikoff.schedulerutmiit.news.data.remote

import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2.NewsCategory
import com.egormelnikoff.schedulerutmiit.news.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsList
import javax.inject.Inject

internal class NewsRemoteDataSource @Inject constructor(
    private val newsApi: NewsApi,
) {
    suspend fun getNewsList(pageSize: Int, page: Int): NewsList =
        newsApi.getNewsList(pageSize, page, page, 2).toDomain()

    suspend fun getNewsListV2(pageSize: Int, page: Int): NewsList {
        return newsApi.getNewsListV2(
            pageSize = pageSize,
            pageNum = page,
            categories = listOf(1363)
        ).toDomain()
    }


    suspend fun getNewsById(id: Long): News =
        newsApi.getNewsById(id).toDomain()
}