package com.egormelnikoff.schedulerutmiit.data.datasource.remote

import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsList
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetables
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MiitApi {
    @GET(Endpoints.GROUPS)
    suspend fun getInstitutes(): Response<Institutes>

    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetables(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): Response<Timetables>

    @GET(Endpoints.SCHEDULE)
    suspend fun getSchedule(
        @Path("type") typeName: String,
        @Path("apiId") apiId: String,
        @Path("timetableId") timetableId: String?
    ): Response<Schedule>

    @GET(Endpoints.NEWS_CATALOG)
    suspend fun getNewsList(
        @Query("page_size") pageSize: Int,
        @Query("from") fromPage: Int,
        @Query("to") toPage: Int
    ): Response<NewsList>

    @GET(Endpoints.NEWS)
    suspend fun getNewsById(
        @Path("newsId") newsId: Long
    ): Response<News>
}