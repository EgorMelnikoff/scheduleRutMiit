package com.egormelnikoff.schedulerutmiit.schedule.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.GroupDto
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.timetable.TimetableDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.timetable.TimetablesDto

interface ScheduleRemoteDataSource {
    suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ): Result<TimetablesDto>

    suspend fun fetchSchedule(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: TimetableDto,
        currentGroup: GroupDto? = null
    ): Result<ScheduleDto>

    suspend fun fetchCurrentWeek(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String
    ): Int
}