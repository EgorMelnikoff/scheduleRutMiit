package com.egormelnikoff.schedulerutmiit.data.repos.paging

import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.domain.repos.NewsRemoteDataSource
import javax.inject.Inject

class PagingNewsSourceFactory @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val resourcesManager: ResourcesManager
) {
    fun create(): PagingNewsSource = PagingNewsSource(newsRemoteDataSource, resourcesManager)
}