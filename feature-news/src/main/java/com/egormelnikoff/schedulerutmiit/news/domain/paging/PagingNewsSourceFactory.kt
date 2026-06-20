package com.egormelnikoff.schedulerutmiit.news.domain.paging

import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRemoteDataSource
import javax.inject.Inject

class PagingNewsSourceFactory @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource
) {
    fun create(): PagingNewsSource = PagingNewsSource(newsRemoteDataSource)
}