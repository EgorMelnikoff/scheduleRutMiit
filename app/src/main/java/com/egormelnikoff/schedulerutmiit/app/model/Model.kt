package com.egormelnikoff.schedulerutmiit.app.model

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.TimetableType
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
    var thumbnail: String,
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
    val content: String,
    var elements: MutableList<Pair<String, Any>>?,
    var images: MutableList<String>?
)


@Keep
data class Subject(
    val title: String,
    val teachers: Set<String>
)