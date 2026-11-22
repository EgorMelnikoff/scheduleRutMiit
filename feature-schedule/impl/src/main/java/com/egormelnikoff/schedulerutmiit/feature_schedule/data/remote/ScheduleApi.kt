package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.TimetablesDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.v2.ScheduleEventDtoV2
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    @Deprecated("Новая ручка")
    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetablesWithoutResponse(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): TimetablesDto

    @Deprecated("Новая ручка")
    @GET(Endpoints.SCHEDULE)
    suspend fun getSchedule(
        @Path("type") type: String,
        @Path("apiId") apiId: String,
        @Path("timetableId") timetableId: String?
    ): ScheduleDto

    @Deprecated("Новая ручка")
    @GET(Endpoints.TIMETABLE)
    suspend fun getTimetables(
        @Path("type") typeName: String,
        @Path("apiId") apiId: Int
    ): Response<TimetablesDto>



    @GET(Endpoints.SCHEDULE_GROUP_V2)
    suspend fun getGroupScheduleV2(
        @Path("groupId") groupId: Int
    ): List<ScheduleEventDtoV2>


    @GET(Endpoints.SCHEDULE_PERSON_V2)
    suspend fun getPersonScheduleV2(
        @Path("personId") personId: Int
    ): List<ScheduleEventDtoV2>
}