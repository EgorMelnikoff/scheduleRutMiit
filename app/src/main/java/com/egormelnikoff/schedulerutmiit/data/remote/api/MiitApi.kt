package com.egormelnikoff.schedulerutmiit.data.remote.api

import com.egormelnikoff.schedulerutmiit.data.remote.dto.institutes.InstitutesDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.news.NewsDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.news.NewsListDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.timetable.TimetablesDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.Endpoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MiitApi {
    @GET(Endpoints.GROUPS)
    suspend fun getInstitutes(): Response<InstitutesDto>

    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetables(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): Response<TimetablesDto>

//    @GET(Endpoints.SCHEDULE)
//    suspend fun getSchedule(
//        @Path("type") typeName: String,
//        @Path("apiId") apiId: String,
//        @Path("timetableId") timetableId: String?
//    ): Response<ScheduleDto>

    @GET(Endpoints.NEWS_CATALOG)
    suspend fun getNewsList(
        @Query("page_size") pageSize: Int,
        @Query("from") fromPage: Int,
        @Query("to") toPage: Int,
        @Query("idk_information_category") idCategory: Int = 2
    ): Response<NewsListDto>

    @GET(Endpoints.NEWS)
    suspend fun getNewsById(
        @Path("newsId") newsId: Long
    ): Response<NewsDto>
}