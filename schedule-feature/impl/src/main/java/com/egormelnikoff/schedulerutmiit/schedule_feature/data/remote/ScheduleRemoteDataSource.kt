package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.HtmlFetcher
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Group
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.ScheduleDto
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.TimetableDto
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.extension.getShortName
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.mapper.ScheduleMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

class ScheduleRemoteDataSource @Inject constructor(
    private val scheduleApi: ScheduleApi,
    private val htmlFetcher: HtmlFetcher,
    private val scheduleMapper: ScheduleMapper,
) {
    suspend fun getSchedule(
        namedScheduleId: Long,
        name: String,
        apiId: Int,
        type: NamedScheduleType
    ): NamedScheduleWithSchedules = coroutineScope {
        val timetables = scheduleApi.getTimetablesWithoutResponse(type.typeName, apiId)

        if (timetables.timetables.isEmpty()) {
            throw IllegalStateException()
        }

        val deferredSchedules = timetables.timetables
            .filter { it.endDate >= LocalDate.now() }
            .mapIndexed { index, timetable ->
                async {
                    val scheduleDto = parseSchedule(
                        timetable, name, apiId, type
                    )

                    scheduleMapper(
                        scheduleDto,
                        namedScheduleId,
                        index
                    )
                }
            }

        val schedules = deferredSchedules.awaitAll()

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

    private suspend fun parseSchedule(
        timetable: TimetableDto,
        name: String,
        apiId: Int,
        type: NamedScheduleType,
    ): ScheduleDto {
        val document = htmlFetcher.invoke(
            url = Endpoints.scheduleUrl(
                type,
                apiId,
                timetable.startDate.toString(),
                timetable.type.id.toString()
            )
        )

        val scheduleDto = ScheduleParser(
            document = document,
            timetable = timetable,
            if (type == NamedScheduleType.GROUP) {
                Group(
                    id = apiId,
                    name = name
                )
            } else null
        )

        return scheduleDto
    }

    suspend fun fetchCurrentWeek(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String
    ): Int {
        htmlFetcher(
            url = Endpoints.scheduleUrl(
                namedScheduleType, apiId, startDate, type
            )
        ).let { document ->
            return ScheduleParser.parseCurrentWeek(document)
        }
    }
}