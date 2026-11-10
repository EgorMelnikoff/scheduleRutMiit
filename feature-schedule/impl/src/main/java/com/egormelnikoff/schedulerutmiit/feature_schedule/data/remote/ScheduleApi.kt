package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.TimetablesDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetablesWithoutResponse(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): TimetablesDto

    @GET(Endpoints.SCHEDULE)
    suspend fun getSchedule(
        @Path("type") type: String,
        @Path("apiId") apiId: String,
        @Path("timetableId") timetableId: String?
    ): ScheduleDto

    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetables(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): Response<TimetablesDto>
}