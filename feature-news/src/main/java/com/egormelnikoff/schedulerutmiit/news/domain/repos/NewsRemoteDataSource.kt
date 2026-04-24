package com.egormelnikoff.schedulerutmiit.news.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.dto.news.NewsListDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.core.common.result.Result

interface NewsRemoteDataSource {
    suspend fun getNewsById(id: Long): Result<NewsParsedDto>
    suspend fun getNewsList(pageSize: Int, page: Int): Result<NewsListDto>
}