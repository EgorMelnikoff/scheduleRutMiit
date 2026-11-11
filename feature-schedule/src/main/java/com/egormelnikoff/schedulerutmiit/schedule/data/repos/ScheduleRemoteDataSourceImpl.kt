package com.egormelnikoff.schedulerutmiit.schedule.data.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.network.api.MiitApi
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.EventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.NonPeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.PeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceEventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.timetable.TimetableDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.helper.NetworkExecutor
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.schedule.data.parser.ScheduleParser
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRemoteDataSource
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
                    document.data.normalize(timetable)
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

    private fun ScheduleDto.normalize(
        timetable: TimetableDto
    ): ScheduleDto {
        return if (timetable.type == TimetableType.PERIODIC) {
            ScheduleDto(
                timetable = timetable,
                periodic = if (this.periodic != null) {
                    this.periodic
                } else {
                    PeriodicContentDto(
                        events = this.nonPeriodic?.events
                            ?.filter { it.name != null }
                            ?.normalizePeriodicEvents(timetable.startDate),
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
                timetable = timetable,
                periodic = null,
                nonPeriodic = NonPeriodicContentDto(
                    events = this.nonPeriodic?.events
                        ?: this.periodic?.events
                )
            )
        }
    }

    private fun List<EventDto>.normalizePeriodicEvents(
        startDate: LocalDate
    ): List<EventDto> {
        return groupBy { it.customHashCode(true) }
            .map { (_, sameEvents) ->
                val event = sameEvents.first()

                if (sameEvents.size > 1) {
                    event.copy(
                        timeSlotName = getTimeSlotName(
                            event.startDatetime,
                            event.endDatetime,
                        ),
                        recurrence = event.recurrence?.copy(
                            interval = 1
                        )
                    )
                } else {
                    event.copy(
                        timeSlotName = getTimeSlotName(
                            event.startDatetime,
                            event.endDatetime,
                        ),
                        periodNumber = getPeriodNumber(
                            event.startDatetime?.toLocalDate(),
                            startDate
                        ),
                        recurrence = RecurrenceEventDto(
                            frequency = "WEEKLY",
                            interval = 2
                        )
                    )
                }
            }
    }

    private fun getPeriodNumber(
        eventDate: LocalDate?,
        scheduleStartDate: LocalDate
    ): Int {
        if (eventDate == null) return 1
        val weeks = ChronoUnit.WEEKS.between(
            scheduleStartDate.getFirstDayOfWeek(),
            eventDate.getFirstDayOfWeek()
        )

        return (weeks % 2).toInt() + 1
    }

}