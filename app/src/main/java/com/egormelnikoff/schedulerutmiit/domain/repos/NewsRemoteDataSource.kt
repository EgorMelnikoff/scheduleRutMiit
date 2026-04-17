package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.local.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.news.NewsListDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result

interface NewsRemoteDataSource {
    suspend fun getNewsById(id: Long): Result<NewsParsedDto>
    suspend fun getNewsList(pageSize: Int, page: Int): Result<NewsListDto>
}