package com.egormelnikoff.schedulerutmiit.news.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2.NewsCategory
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2.NewsDtoV2
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2.NewsListDtoV2
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface NewsApi {
    @GET(Endpoints.NEWS_CATEGORIES_V2)
    suspend fun getCategories(): List<NewsCategory>

    @GET(Endpoints.NEWS_LIST_V2)
    suspend fun getNewsListV2(
        @Query("isGeneral") isGeneral: Boolean = true,
        @Query("categories") categories: List<Int> = emptyList(),
        @Query("chunkSize") pageSize: Int,
        @Query("pageNum") pageNum: Int,
    ): NewsListDtoV2

    @GET(Endpoints.NEWS_V2)
    suspend fun getNewsV2(
        @Path("newsId") newsId: Long,
    ): NewsDtoV2

}