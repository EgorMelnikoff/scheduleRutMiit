package com.egormelnikoff.schedulerutmiit.datasource.remote.news.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egormelnikoff.schedulerutmiit.app.network.model.NewsShortModel
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.datasource.remote.news.NewsRemoteDataSource

class PagingNewsSource(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val resourcesManager: ResourcesManager
) : PagingSource<Int, NewsShortModel>() {
    override fun getRefreshKey(state: PagingState<Int, NewsShortModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, NewsShortModel> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        return when (val response = newsRemoteDataSource.getNewsList(pageSize, currentPage)) {
            is Result.Error -> {
                LoadResult.Error(
                    Exception(
                        TypedError.getErrorMessage(
                            resourcesManager,
                            response.typedError
                        )
                    )
                )
            }

            is Result.Success -> {
                val nextKey = if (currentPage < response.data.maxPage) currentPage + 1 else null

                val updatedItems = response.data.items.filter { it.secondary.text != "Наши защиты" }

                LoadResult.Page(
                    data = updatedItems,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = nextKey
                )
            }
        }
    }
}