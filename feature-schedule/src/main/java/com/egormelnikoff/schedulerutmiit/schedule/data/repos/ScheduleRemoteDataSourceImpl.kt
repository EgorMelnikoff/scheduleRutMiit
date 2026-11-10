package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.EventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.NonPeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.PeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.timetable.TimetableDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkExecutor
import com.egormelnikoff.schedulerutmiit.schedule.data.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRemoteDataSource
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

class ScheduleRemoteDataSourceImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val networkExecutor: NetworkExecutor
) : ScheduleRemoteDataSource {
    override suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ) = networkExecutor.callApi {
        miitApi.getTimetables(type.typeName, apiId)
    }

    override suspend fun fetchSchedule(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: TimetableDto,
        currentGroup: Group?
    ): Result<ScheduleDto> {
        networkExecutor.callApi {
            miitApi.getSchedule(
                namedScheduleType.typeName,
                apiId.toString(),
                timetable.id
            )
        }.let { document ->
            return when (document) {
                is Result.Error -> document

                is Result.Success -> Result.Success(
                    document.data.normalize()
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
        networkExecutor.callHtml(
            url = Endpoints.scheduleUrl(
                namedScheduleType, apiId, startDate, type
            )
        ).let { document ->
            return when (document) {
                is Result.Error -> 1

                is Result.Success -> {
                    ScheduleParser.parseCurrentWeek(document.data)
                }
            }
        }
    }

    private fun ScheduleDto.normalize(): ScheduleDto {
        return if (this.timetable.type == TimetableType.PERIODIC) {
            ScheduleDto(
                timetable = this.timetable,
                periodic = if (this.periodic != null) {
                    this.periodic
                } else {
                    PeriodicContentDto(
                        events = this.nonPeriodic?.events?.normalizePeriodicEvents(),
                        recurrence = RecurrenceDto(
                            interval = 2,
                            currentNumber = 1
                        )
                    )
                },
                nonPeriodic = null
            )
        } else {
            ScheduleDto(
                timetable = this.timetable,
                periodic = null,
                nonPeriodic = NonPeriodicContentDto(
                    events = this.nonPeriodic?.events
                        ?: this.periodic?.events
                )
            )
        }
    }

    private fun List<EventDto>.normalizePeriodicEvents(): List<EventDto> {
        return this
            .groupBy { it.customHashCode(true) }
            .flatMap { (_, events) ->
                events
                    .groupBy { it.collisionKey() }
                    .map { (_, sameEvents) ->
                        sameEvents.first().let { event ->
                            if (sameEvents.size > 1) {
                                event.copy(
                                    recurrence = event.recurrence?.copy(interval = 1)
                                )
                            } else {
                                event
                            }
                        }
                    }
            }
    }

    private fun EventDto.collisionKey() =
        rooms to lecturers

}