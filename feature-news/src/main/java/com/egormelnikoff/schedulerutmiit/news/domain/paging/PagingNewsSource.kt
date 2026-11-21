package com.egormelnikoff.schedulerutmiit.news.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsShort
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRepos

internal class PagingNewsSource(
    private val newsRemoteDataSource: NewsRepos
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

        val response = newsRemoteDataSource.getNewsList(pageSize, currentPage)

        val nextKey = if (currentPage < response.maxPage) currentPage + 1 else null

        val updatedItems = response.items.filter { it.text != "Наши защиты" }

        return LoadResult.Page(
            data = updatedItems,
            prevKey = if (currentPage == 1) null else currentPage - 1,
            nextKey = nextKey
        )
    }
}