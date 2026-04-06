package com.egormelnikoff.schedulerutmiit.datasource.remote.news

import com.egormelnikoff.schedulerutmiit.app.dto.local.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.news.NewsListDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

interface NewsRemoteDataSource {
    suspend fun getNewsById(id: Long): Result<NewsParsedDto>
    suspend fun getNewsList(pageSize: Int, page: Int): Result<NewsListDto>
}