package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.timetable.TimetableDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkHelper
import com.egormelnikoff.schedulerutmiit.schedule.data.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRemoteDataSource
import javax.inject.Inject

class ScheduleRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val scheduleParser: ScheduleParser,
    private val networkHelper: NetworkHelper
) : ScheduleRemoteDataSource {
    override suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ) = networkHelper.callApi {
        miitApi.getTimetables(type.typeName, apiId)
    }

    override suspend fun fetchSchedule(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: TimetableDto,
        currentGroup: Group?
    ): Result<ScheduleDto> {
        networkHelper.callHtml(
            url = Endpoints.scheduleUrl(
                namedScheduleType,
                apiId,
                timetable.startDate.toString(),
                timetable.type.id.toString()
            )
        ).let { document ->
            return when (document) {
                is Result.Error -> document

                is Result.Success -> Result.Success(
                    scheduleParser(
                        document.data,
                        timetable,
                        currentGroup
                    )
                )
            }
        }
    }

    override suspend fun fetchCurrentWeek(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String
    ): Int {
        networkHelper.callHtml(
            url = Endpoints.scheduleUrl(
                namedScheduleType, apiId, startDate, type
            )
        ).let { document ->
            return when (document) {
                is Result.Error -> 1

                is Result.Success -> {
                    scheduleParser.parseCurrentWeek(document.data)
                }
            }
        }
    }
}