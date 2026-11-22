package com.egormelnikoff.schedulerutmiit.news.data.remote

import com.egormelnikoff.schedulerutmiit.news.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsList
import javax.inject.Inject

internal class NewsRemoteDataSource @Inject constructor(
    private val newsApi: NewsApi,
) {
    suspend fun getNewsListV2(pageSize: Int, page: Int): NewsList {
        return newsApi.getNewsListV2(
            pageSize = pageSize,
            pageNum = page,
            categories = listOf(1363)
        ).toDomain()
    }

    suspend fun getNewsV2(id: Long): News {
        return newsApi.getNewsV2(id).toDomain()
    }
}