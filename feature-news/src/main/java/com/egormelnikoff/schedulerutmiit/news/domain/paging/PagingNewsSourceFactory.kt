package com.egormelnikoff.schedulerutmiit.news.domain.paging

import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRepos
import javax.inject.Inject

internal class PagingNewsSourceFactory @Inject constructor(
    private val newsRemoteDataSource: NewsRepos
) {
    fun create(): PagingNewsSource = PagingNewsSource(newsRemoteDataSource)
}