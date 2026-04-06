package com.egormelnikoff.schedulerutmiit.datasource.remote.schedule

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable.TimetableDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.timetable.TimetablesDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

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