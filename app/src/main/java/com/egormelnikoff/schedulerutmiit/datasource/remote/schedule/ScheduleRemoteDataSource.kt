package com.egormelnikoff.schedulerutmiit.datasource.remote.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.network.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.network.model.Timetable
import com.egormelnikoff.schedulerutmiit.app.network.model.Timetables
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

interface ScheduleRemoteDataSource {
    suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ): Result<Timetables>

    suspend fun fetchSchedule(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: Timetable,
        currentGroup: Group? = null
    ): Result<Schedule>

    suspend fun fetchCurrentWeek(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String
    ): Int
}