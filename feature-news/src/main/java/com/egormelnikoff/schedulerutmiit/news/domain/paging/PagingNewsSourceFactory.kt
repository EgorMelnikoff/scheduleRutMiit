package com.egormelnikoff.schedulerutmiit.news.domain.paging

import com.egormelnikoff.schedulerutmiit.core.common.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRemoteDataSource
import javax.inject.Inject

class PagingNewsSourceFactory @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val resourcesManager: ResourcesManager
) {
    fun create(): PagingNewsSource = PagingNewsSource(newsRemoteDataSource, resourcesManager)
}