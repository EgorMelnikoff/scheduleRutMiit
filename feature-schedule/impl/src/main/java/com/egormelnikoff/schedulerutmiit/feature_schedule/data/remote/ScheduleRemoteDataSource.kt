package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper.ScheduleMapperV2
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getShortName
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import javax.inject.Inject

internal class ScheduleRemoteDataSource @Inject constructor(
    private val scheduleApi: ScheduleApi,
    private val scheduleMapperV2: ScheduleMapperV2
) {
    suspend fun getSchedule(
        savedId: Long,
        name: String,
        apiId: Int,
        type: ScheduleType
    ): NamedScheduleWithSchedules = coroutineScope {
        val scheduleDto = when (type) {
            ScheduleType.GROUP -> scheduleApi.getGroupScheduleV2(apiId)
            ScheduleType.PERSON -> scheduleApi.getPersonScheduleV2(apiId)
            else -> emptyList()
        }

        val schedules = scheduleMapperV2.map(
            namedScheduleId = savedId,
            scheduleType = type,
            apiId = apiId,
            dto = scheduleDto
        )

        val namedSchedule = NamedSchedule(
            id = savedId,
            fullName = name,
            shortName = name.getShortName(type),
            apiId = apiId.toString(),
            type = type,
            isDefault = false,
            lastTimeUpdate = LocalDateTime.now()
        )

        NamedScheduleWithSchedules(
            namedSchedule = namedSchedule,
            schedulesWithEvents = schedules
        )
    }
}