package com.egormelnikoff.schedulerutmiit.app.model

import androidx.annotation.Keep
import androidx.room.Embedded
import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.Lecturer
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.RecurrenceRule
import com.egormelnikoff.schedulerutmiit.app.entity.Room
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
data class Schedule(
    val timetable: Timetable,
    val periodicContent: PeriodicContent?,
    val nonPeriodicContent: NonPeriodicContent?
)

@Keep
data class Timetables(
    val timetables: List<Timetable>
)

@Keep
data class Timetable(
    val id: String,
    val name: String,
    val type: TimetableType,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate
)

@Keep
data class PeriodicContent(
    val events: List<Event>?,
    val recurrence: Recurrence
)

@Keep
data class NonPeriodicContent(
    val events: List<Event>?,
)

@Keep
data class Event(
    val startDatetime: LocalDateTime?,
    val endDatetime: LocalDateTime?,
    @Embedded
    val recurrenceRule: RecurrenceRule?,
    val periodNumber: Int?,
    val name: String?,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<Lecturer>?,
    val rooms: List<Room>?,
    val groups: List<Group>?
) {
    fun customHashCode(forceNonPeriodic: Boolean = false): Int {
        val hashString = when {
            forceNonPeriodic -> "$name$typeName${startDatetime?.dayOfWeek}${startDatetime?.toLocalTime()}$groups"
            (recurrenceRule != null) -> "$name$typeName${startDatetime?.dayOfWeek}${startDatetime?.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
            else -> "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    fun toEntity() = if (startDatetime != null && endDatetime != null && name != null)
        EventEntity(
            startDatetime = startDatetime,
            endDatetime = endDatetime,
            recurrenceRule = recurrenceRule,
            periodNumber = periodNumber,
            name = name,
            typeName = typeName,
            timeSlotName = timeSlotName,
            lecturers = lecturers,
            groups = groups,
            rooms = rooms
        )
    else throw NullPointerException()
}


@Keep
data class Institutes(
    val institutes: List<Institute>
)

@Keep
data class Institute(
    val id: Int,
    val name: String,
    val abbreviation: String,
    val courses: List<Course>
)

@Keep
data class Course(
    val course: String,
    val specialties: List<Specialty>
)

@Keep
data class Specialty(
    val name: String,
    val abbreviation: String,
    val groups: List<Group>
)

@Keep
data class Person(
    val name: String,
    val id: Int,
    val position: String
)


@Keep
data class NewsList(
    val maxPage: Int,
    val items: List<NewsShort>
)

@Keep
data class NewsShort(
    val idInformation: Long,
    val title: String,
    val date: LocalDateTime,
    val thumbnail: String,
    val secondary: Secondary
)

@Keep
data class Secondary(
    val text: String
)

@Keep
data class News(
    val idInformation: Long,
    val title: String,
    val hisdateDisplay: String,
    val content: String
)

@Keep
data class NewsContent(
    val news: News,
    val elements: MutableList<Pair<String, Any>>,
    val images: MutableList<String>
)


@Keep
data class Subject(
    val title: String,
    val teachers: Set<String>
)