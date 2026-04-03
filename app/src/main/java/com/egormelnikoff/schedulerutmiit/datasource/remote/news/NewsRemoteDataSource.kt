package com.egormelnikoff.schedulerutmiit.datasource.remote.news

import com.egormelnikoff.schedulerutmiit.app.network.model.NewsContent
import com.egormelnikoff.schedulerutmiit.app.network.model.NewsListModel
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

interface NewsRemoteDataSource {
    suspend fun getNewsById(id: Long): Result<NewsContent>
    suspend fun getNewsList(pageSize: Int, page: Int): Result<NewsListModel>
}