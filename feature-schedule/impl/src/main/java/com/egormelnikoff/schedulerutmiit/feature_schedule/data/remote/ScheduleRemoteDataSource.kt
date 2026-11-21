package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper.ScheduleMapperV2
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.EventDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.NonPeriodicContentDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.PeriodicContentDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.RecurrenceEventDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.TimetableDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getShortName
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper.ScheduleMapperV1
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ScheduleRemoteDataSource @Inject constructor(
    private val scheduleApi: ScheduleApi,
    private val scheduleMapperV1: ScheduleMapperV1,
    private val scheduleMapperV2: ScheduleMapperV2
) {
    suspend fun getSchedule(
        namedScheduleId: Long,
        name: String,
        apiId: Int,
        type: NamedScheduleType
    ): NamedScheduleWithSchedules = coroutineScope {
        val scheduleDto = when (type) {
            NamedScheduleType.GROUP -> scheduleApi.getGroupScheduleV2(apiId)
            NamedScheduleType.PERSON -> scheduleApi.getPersonScheduleV2(apiId)
            NamedScheduleType.ROOM -> emptyList()
            else -> emptyList()
        }

        val schedules = scheduleMapperV2.map(
            namedScheduleId = namedScheduleId,
            namedScheduleType = type,
            apiId = apiId,
            dto = scheduleDto
        )

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


    suspend fun getScheduleOld(
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
                    val scheduleDto = scheduleApi.getSchedule(
                        type.typeName,
                        apiId.toString(),
                        timetable.id
                    ).normalizeOld(timetable)

                    scheduleMapperV1.map(
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

    private fun ScheduleDto.normalizeOld(
        timetable: TimetableDto
    ): ScheduleDto {
        return if (timetable.type == TimetableType.PERIODIC) {
            ScheduleDto(
                timetable = timetable,
                periodic = this.periodic
                    ?: PeriodicContentDto(
                        events = this.nonPeriodic?.events
                            ?.filter { it.name != null }
                            ?.normalizePeriodicEvents(timetable.startDate),
                        recurrence = RecurrenceDto(
                            interval = 2,
                            currentNumber = 1
                        )
                    ),
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