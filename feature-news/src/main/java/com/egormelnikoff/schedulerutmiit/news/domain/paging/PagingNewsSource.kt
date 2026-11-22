package com.egormelnikoff.schedulerutmiit.news.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsShort
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRepos
import okio.IOException
import retrofit2.HttpException

internal class PagingNewsSource(
    private val newsRepos: NewsRepos
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
        return try {
            val currentPage = params.key ?: 1
            val pageSize = params.loadSize

            val response = newsRepos.getNewsListV2(pageSize, currentPage - 1)

            val nextKey = if (currentPage < response.maxPage) currentPage + 1 else null

            LoadResult.Page(
                data = response.items,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}