package com.egormelnikoff.schedulerutmiit.news.data.repos

import com.egormelnikoff.schedulerutmiit.news.data.remote.NewsRemoteDataSource
import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsList
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRepos
import javax.inject.Inject

internal  class NewsReposImpl @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource
) : NewsRepos {
    override suspend fun getNewsList(pageSize: Int, page: Int) =
        newsRemoteDataSource.getNewsList(pageSize, page)

    override suspend fun getNewsById(id: Long) =
        newsRemoteDataSource.getNewsById(id)

    override suspend fun getNewsListV2(pageSize: Int, page: Int) =
        newsRemoteDataSource.getNewsListV2(pageSize, page)

    override suspend fun getNewsV2(id: Long): News =
        newsRemoteDataSource.getNewsV2(id)
}