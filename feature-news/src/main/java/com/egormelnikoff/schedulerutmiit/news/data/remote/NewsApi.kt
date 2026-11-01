package com.egormelnikoff.schedulerutmiit.news.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.NewsDto
import com.egormelnikoff.schedulerutmiit.news.data.remote.dto.NewsListDto
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

    @GET(Endpoints.NEWS)
    suspend fun getNewsById(
        @Path("newsId") newsId: Long
    ): NewsDto
}