package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper.ScheduleMapperV2
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getShortName
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class ScheduleRemoteDataSource @Inject constructor(
    private val scheduleApi: ScheduleApi,
    private val scheduleMapperV2: ScheduleMapperV2
) {
    suspend fun getSchedule(
        namedScheduleId: Long,
        name: String,
        apiId: Int,
        type: NamedScheduleType
    ): NamedScheduleWithSchedules = coroutineScope {
        val scheduleDto = when (type) {
            NamedScheduleType.GROUP -> scheduleApi.getGroupScheduleV2(apiId)
            NamedScheduleType.PERSON -> scheduleApi.getPersonScheduleV2(apiId)
            NamedScheduleType.ROOM -> emptyList()
            else -> emptyList()
        }

        val schedules = scheduleMapperV2.map(
            namedScheduleId = namedScheduleId,
            namedScheduleType = type,
            apiId = apiId,
            dto = scheduleDto
        )

        val namedSchedule = NamedSchedule(
            id = namedScheduleId,
            fullName = name,
            shortName = name.getShortName(type),
            apiId = apiId.toString(),
            type = type,
            isDefault = false,
            lastTimeUpdate = System.currentTimeMillis()
        )

        NamedScheduleWithSchedules(
            namedSchedule = namedSchedule,
            schedulesWithEvents = schedules
        )
    }
}