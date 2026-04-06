package com.egormelnikoff.schedulerutmiit.domain.news

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egormelnikoff.schedulerutmiit.app.dto.remote.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.datasource.remote.news.paging.PagingNewsSourceFactory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsListUseCase @Inject constructor(
    private val pagingSourceFactory: PagingNewsSourceFactory
) {
    operator fun invoke(): Flow<PagingData<NewsShortDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { pagingSourceFactory.create() }
        ).flow
    }
}