package com.egormelnikoff.schedulerutmiit.news.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.NewsDto
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.NewsListDto
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2.NewsCategory
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.v2.NewsListDtoV2
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface NewsApi {
    @GET(Endpoints.NEWS_CATALOG)
    suspend fun getNewsList(
        @Query("page_size") pageSize: Int,
        @Query("from") fromPage: Int,
        @Query("to") toPage: Int,
        @Query("idk_information_category") idCategory: Int
    ): NewsListDto


    @GET(Endpoints.NEWS_CATEGORIES_V2)
    suspend fun getCategories(): List<NewsCategory>

    @GET(Endpoints.NEWS_LIST_V2)
    suspend fun getNewsListV2(
        @Query("isGeneral") isGeneral: Boolean = true,
        @Query("categories") categories: List<Int> = emptyList(),
        @Query("chunkSize") pageSize: Int,
        @Query("pageNum") pageNum: Int,
    ): NewsListDtoV2

    @GET(Endpoints.NEWS)
    suspend fun getNewsById(
        @Path("newsId") newsId: Long
    ): NewsDto
}