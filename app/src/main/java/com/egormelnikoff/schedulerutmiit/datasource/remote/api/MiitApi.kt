package com.egormelnikoff.schedulerutmiit.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.network.Endpoints
import com.egormelnikoff.schedulerutmiit.app.network.model.InstitutesModel
import com.egormelnikoff.schedulerutmiit.app.network.model.NewsModel
import com.egormelnikoff.schedulerutmiit.app.network.model.NewsListModel
import com.egormelnikoff.schedulerutmiit.app.network.model.ScheduleModel
import com.egormelnikoff.schedulerutmiit.app.network.model.TimetablesModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MiitApi {
    @GET(Endpoints.GROUPS)
    suspend fun getInstitutes(): Response<InstitutesModel>

    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetables(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): Response<TimetablesModel>

    @GET(Endpoints.SCHEDULE)
    suspend fun getSchedule(
        @Path("type") typeName: String,
        @Path("apiId") apiId: String,
        @Path("timetableId") timetableId: String?
    ): Response<ScheduleModel>

    @GET(Endpoints.NEWS_CATALOG)
    suspend fun getNewsList(
        @Query("page_size") pageSize: Int,
        @Query("from") fromPage: Int,
        @Query("to") toPage: Int
    ): Response<NewsListModel>

    @GET(Endpoints.NEWS)
    suspend fun getNewsById(
        @Path("newsId") newsId: Long
    ): Response<NewsModel>
}