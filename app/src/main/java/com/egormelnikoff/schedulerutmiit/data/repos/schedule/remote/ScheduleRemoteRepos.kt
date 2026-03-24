package com.egormelnikoff.schedulerutmiit.data.repos.schedule.remote

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetable
import com.egormelnikoff.schedulerutmiit.app.model.Timetables
import com.egormelnikoff.schedulerutmiit.data.Result

interface ScheduleRemoteRepos {
    suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ): Result<Timetables>

    suspend fun fetchScheduleApi(
        namedScheduleType: NamedScheduleType,
        apiId: String,
        timetableId: String
    ): Result<Schedule>

    suspend fun fetchScheduleParser(
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