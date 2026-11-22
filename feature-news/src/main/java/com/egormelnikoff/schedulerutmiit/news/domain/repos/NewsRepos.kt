package com.egormelnikoff.schedulerutmiit.news.domain.repos

import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsList

internal interface NewsRepos {
    suspend fun getNewsListV2(pageSize: Int, page: Int): NewsList
    suspend fun getNewsV2(id: Long): News
}