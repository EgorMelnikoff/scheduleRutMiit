package com.egormelnikoff.schedulerutmiit.data.datasource.remote.api

import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsList
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetables
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper.Companion.GROUPS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper.Companion.NEWS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper.Companion.NEWS_CATALOG
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper.Companion.SCHEDULE
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper.Companion.TIMETABLE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MiitApi {
    @GET(GROUPS)
    suspend fun getInstitutes(): Response<Institutes>

    @GET(TIMETABLE)
    suspend fun getTimetables(
        @Path("type") type: String,
        @Path("apiId") apiId: String
    ): Response<Timetables>

    @GET(SCHEDULE)
    suspend fun getSchedule(
        @Path("type") type: String,
        @Path("apiId") apiId: String,
        @Path("timetableId") timetableId: String?
    ): Response<Schedule>

    @GET(NEWS_CATALOG)
    suspend fun getNewsList(
        @Query("from") fromPage: String,
        @Query("to") toPage: String
    ): Response<NewsList>

    @GET(NEWS)
    suspend fun getNewsById(
        @Path("newsId") newsId: Long
    ): Response<News>
}