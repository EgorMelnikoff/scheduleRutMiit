package com.egormelnikoff.schedulerutmiit.news.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsListDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto

interface NewsRemoteDataSource {
    suspend fun getNewsById(id: Long): Result<NewsParsedDto>
    suspend fun getNewsList(pageSize: Int, page: Int): Result<NewsListDto>
}