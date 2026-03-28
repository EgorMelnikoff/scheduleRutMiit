package com.egormelnikoff.schedulerutmiit.datasource.remote.news.paging

import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.datasource.remote.news.NewsRemoteDataSource
import javax.inject.Inject

class PagingNewsSourceFactory @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val resourcesManager: ResourcesManager
) {
    fun create(): PagingNewsSource = PagingNewsSource(newsRemoteDataSource, resourcesManager)
}