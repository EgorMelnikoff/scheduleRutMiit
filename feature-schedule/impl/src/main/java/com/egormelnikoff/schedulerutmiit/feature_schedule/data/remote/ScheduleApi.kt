package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleEventDtoV2
import retrofit2.http.GET
import retrofit2.http.Path

internal interface ScheduleApi {
    @GET(Endpoints.SCHEDULE_GROUP_V2)
    suspend fun getGroupScheduleV2(
        @Path("groupId") groupId: Int
    ): List<ScheduleEventDtoV2>


    @GET(Endpoints.SCHEDULE_PERSON_V2)
    suspend fun getPersonScheduleV2(
        @Path("personId") personId: Int
    ): List<ScheduleEventDtoV2>
}