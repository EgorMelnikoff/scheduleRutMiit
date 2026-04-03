package com.egormelnikoff.schedulerutmiit.domain.news

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egormelnikoff.schedulerutmiit.app.network.model.NewsShortModel
import com.egormelnikoff.schedulerutmiit.datasource.remote.news.paging.PagingNewsSourceFactory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsListUseCase @Inject constructor(
    private val pagingSourceFactory: PagingNewsSourceFactory
) {
    operator fun invoke(): Flow<PagingData<NewsShortModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { pagingSourceFactory.create() }
        ).flow
    }
}