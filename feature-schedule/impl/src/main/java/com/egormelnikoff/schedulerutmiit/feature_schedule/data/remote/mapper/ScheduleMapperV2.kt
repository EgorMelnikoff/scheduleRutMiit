package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleEventDtoV2
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getShortName
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Group
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Room
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.abs

internal class ScheduleMapperV2 @Inject constructor() {
    fun map(
        namedScheduleId: Long,
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        dto: List<ScheduleEventDtoV2>
    ): List<ScheduleWithEvents> {
        val groupedItems = dto.groupBy {
            ScheduleGroupKey(
                timetableType = it.timetableType.mapToTimetableType(),
                valueFrom = it.validFrom.toLocalDate(),
                valueTo = it.validTo.toLocalDate(),
            )
        }

        val today = LocalDate.now()
        var isDefaultAssigned = false

        return groupedItems.map { (schedule, items) ->
            val isDefault = !isDefaultAssigned && today in schedule.valueFrom..schedule.valueTo
            if (isDefault) isDefaultAssigned = true

            ScheduleWithEvents(
                schedule = Schedule(
                    namedScheduleId = namedScheduleId,
                    timetableId = "${schedule.valueFrom}${schedule.timetableType.id}",
                    downloadUrl = Endpoints.downloadUrl(
                        isPerson = namedScheduleType.isPerson(),
                        apiId = apiId,
                        type = schedule.timetableType.id
                    ),
                    timetableType = schedule.timetableType,
                    startDate = schedule.valueFrom,
                    endDate = schedule.valueTo,
                    recurrence = if (schedule.timetableType.isPeriodic()) {
                        buildRecurrence(
                            interval = 2,
                            currentNumber = items.getCurrentPeriodNumber() ?: 1,
                            startDate = schedule.valueFrom
                        )
                    } else {
                        null
                    },
                    isDefault = isDefault
                ),
                events = if (schedule.timetableType.isPeriodic()) {
                    items.mapPeriodic()
                } else {
                    items.mapNonPeriodic()
                }
            )
        }
    }

    private fun NamedScheduleType.isPerson(): Boolean {
        return if (this == NamedScheduleType.PERSON) true else false
    }

    private fun Int.mapToTimetableType(): TimetableType {
        return when (this) {
            1 -> TimetableType.PERIODIC
            2 -> TimetableType.NON_PERIODIC
            4 -> TimetableType.SESSION
            else -> TimetableType.NON_PERIODIC
        }
    }

    fun List<ScheduleEventDtoV2>.getCurrentPeriodNumber(): Int? {
        return firstOrNull { it.isCurrentWeek == 1 }
            ?.periodNumber
    }

    private fun List<ScheduleEventDtoV2>.mapPeriodic(): List<Event> {
        return groupBy { it.eventId to it.groups }
            .map { (_, sameEvents) ->
                val item = sameEvents.first()

                if (sameEvents.size > 1) {
                    buildEvent(
                        item = item,
                        interval = 1,
                        periodNumber = null,
                    )
                } else {
                    buildEvent(
                        item = item,
                        interval = 2,
                        periodNumber = item.periodNumber,
                    )
                }
            }
    }


    private fun List<ScheduleEventDtoV2>.mapNonPeriodic(): List<Event> {
        return this.map { item ->
            buildEvent(
                item = item,
                interval = null,
                periodNumber = null
            )
        }
    }

    private fun buildEvent(
        item: ScheduleEventDtoV2,
        interval: Int?,
        periodNumber: Int?
    ): Event {
        return Event(
            isHidden = false,
            isCustomEvent = false,
            startDatetime = item.startDateTime,
            endDatetime = item.endDateTime,
            interval = interval,
            periodNumber = periodNumber,
            name = item.name,
            typeName = item.typeName,
            timeSlotName = item.timeSlotName,
            lecturers = item.lecturers?.parseLecturers(),
            rooms = item.rooms?.parseRooms(),
            groups = item.groups?.parseGroups()
        )
    }

    private fun buildRecurrence(
        interval: Int,
        currentNumber: Int,
        startDate: LocalDate
    ): Recurrence {
        val today = LocalDate.now()
        return if (today > startDate && interval > 1) {
            val currentWeekIndex = abs(
                ChronoUnit.WEEKS.between(
                    startDate.getFirstDayOfWeek(),
                    today.getFirstDayOfWeek()
                )
            ).plus(1)

            val firstWeekNumber =
                ((currentWeekIndex + currentNumber) % interval)
                    .plus(1)

            Recurrence(
                interval,
                firstWeekNumber = firstWeekNumber.toInt()
            )
        } else {
            Recurrence(
                interval,
                firstWeekNumber = currentNumber
            )
        }
    }

    private fun String.parseRooms(): List<Room> {
        if (this.isBlank()) return emptyList()

        val document = Jsoup.parse(this, "", Parser.xmlParser())

        return document.select("premise").map { element ->
            Room(
                id = element.selectFirst("id")?.text()?.toIntOrNull() ?: -1,
                name = element.selectFirst("name")?.text().orEmpty(),
                hint = element.selectFirst("hint")?.text().orEmpty()
            )
        }
    }

    private fun String.parseLecturers(): List<Lecturer> {
        if (this.isBlank()) return emptyList()

        val document = Jsoup.parse(this, "", Parser.xmlParser())


        return document.select("lecturer").map { lecturer ->
            val fullFio = lecturer.selectFirst("full_fio")?.text()
            val shortFio = lecturer.selectFirst("short_fio")?.text().orEmpty()

            Lecturer(
                id = lecturer.selectFirst("id")?.text()?.toIntOrNull() ?: -1,
                shortFio = if (fullFio == null) shortFio.getShortName(
                    NamedScheduleType.PERSON
                ) else shortFio,
                fullFio = fullFio ?: shortFio,
                hint = lecturer.selectFirst("description")?.text().orEmpty(),
            )
        }
    }


    private fun String.parseGroups(): List<Group> {
        if (this.isBlank()) return emptyList()

        val document = Jsoup.parse(this, "", Parser.xmlParser())

        return document.select("ed_group").map { group ->
            Group(
                id = group.selectFirst("id")?.text()?.toIntOrNull() ?: -1,
                name = group.selectFirst("name")?.text().orEmpty(),
            )
        }
    }
}

internal data class ScheduleGroupKey(
    val valueFrom: LocalDate,
    val valueTo: LocalDate,
    val timetableType: TimetableType
)