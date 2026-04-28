package com.egormelnikoff.schedulerutmiit.news.domain.use_case

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.news.domain.paging.PagingNewsSourceFactory
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