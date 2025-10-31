package com.egormelnikoff.schedulerutmiit.data.repos.news

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egormelnikoff.schedulerutmiit.app.model.NewsShort
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper


class PagingNewsSource (
    private val miitApi: MiitApi,
    private val miitApiHelper: MiitApiHelper,
    private val resourcesManager: ResourcesManager
) : PagingSource<Int, NewsShort>() {
    override fun getRefreshKey(state: PagingState<Int, NewsShort>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, NewsShort> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        val response = miitApiHelper.callApiWithExceptions(
            fetchDataType = "News list",
            message = "From page: $currentPage; To page: $currentPage"
        ) {
            miitApi.getNewsList(pageSize, currentPage, currentPage)
        }

        return when (response) {
            is Result.Error -> {
                LoadResult.Error(Exception(Error.getErrorMessage(resourcesManager, response.error)))
            }

            is Result.Success -> {
                val nextKey = if (currentPage < response.data.maxPage) currentPage + 1 else null

                val updatedItems = response.data.items
                    .filter { it.secondary.text != "Наши защиты" }
                    .map { newsShort ->
                        newsShort.apply { thumbnail = "https://www.miit.ru$thumbnail" }
                    }

                LoadResult.Page(
                    data = updatedItems,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = nextKey
                )

            }
        }
    }
}