package com.egormelnikoff.schedulerutmiit.news.domain.repos

import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsList

internal interface NewsRepos {
    suspend fun getNewsById(id: Long): News
    suspend fun getNewsList(pageSize: Int, page: Int): NewsList
}