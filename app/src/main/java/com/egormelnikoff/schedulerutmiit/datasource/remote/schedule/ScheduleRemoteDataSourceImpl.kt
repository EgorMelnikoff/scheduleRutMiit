package com.egormelnikoff.schedulerutmiit.datasource.remote.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.network.Endpoints
import com.egormelnikoff.schedulerutmiit.app.network.NetworkHelper
import com.egormelnikoff.schedulerutmiit.app.network.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.network.model.Timetable
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.datasource.local.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.datasource.remote.api.MiitApi
import org.jsoup.Jsoup
import javax.inject.Inject

class ScheduleRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val scheduleParser: ScheduleParser,
    private val networkHelper: NetworkHelper
) : ScheduleRemoteDataSource {
    override suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ) = networkHelper.callNetwork(
        requestType = "Timetables",
        requestParams = "Type: $type; ApiId: $apiId",
        timeoutMs = 5000,
        callApi = {
            miitApi.getTimetables(type.typeName, apiId)
        },
        callJsoup = null
    )

    override suspend fun fetchSchedule(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: Timetable,
        currentGroup: Group?
    ): Result<Schedule> {
        networkHelper.callNetwork(
            requestType = "ScheduleParser",
            requestParams = "Id: $apiId; Type: $namedScheduleType; Start date: ${timetable.startDate}",
            timeoutMs = 10000,
            callJsoup = {
                Jsoup.connect(
                    Endpoints.scheduleUrl(
                        namedScheduleType,
                        apiId,
                        timetable.startDate.toString(),
                        timetable.type.id.toString()
                    )
                ).get()
            },
            callApi = null
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
        networkHelper.callNetwork(
            requestType = "CurrentWeek",
            requestParams = "id: $apiId",
            callJsoup = {
                Jsoup.connect(
                    Endpoints.scheduleUrl(
                        namedScheduleType, apiId, startDate, type
                    )
                ).get()
            },
            callApi = null
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