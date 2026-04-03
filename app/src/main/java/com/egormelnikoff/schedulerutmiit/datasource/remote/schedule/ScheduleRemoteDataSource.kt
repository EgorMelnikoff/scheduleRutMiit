package com.egormelnikoff.schedulerutmiit.datasource.remote.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.network.model.ScheduleModel
import com.egormelnikoff.schedulerutmiit.app.network.model.TimetableModel
import com.egormelnikoff.schedulerutmiit.app.network.model.TimetablesModel
import com.egormelnikoff.schedulerutmiit.app.network.result.Result

interface ScheduleRemoteDataSource {
    suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ): Result<TimetablesModel>

    suspend fun fetchSchedule(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: TimetableModel,
        currentGroup: Group? = null
    ): Result<ScheduleModel>

    suspend fun fetchCurrentWeek(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String
    ): Int
}