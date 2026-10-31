package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.TimetablesDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetablesWithoutResponse(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): TimetablesDto

    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetables(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): Response<TimetablesDto>
}