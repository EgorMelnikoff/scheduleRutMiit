package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.timetable.TimetableDto
import com.egormelnikoff.schedulerutmiit.data.remote.dto.timetable.TimetablesDto
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result

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