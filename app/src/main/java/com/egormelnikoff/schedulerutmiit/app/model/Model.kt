package com.egormelnikoff.schedulerutmiit.app.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Schedule(
    val timetable: Timetable?,
    val periodicContent: PeriodicContent?,
    val nonPeriodicContent: NonPeriodicContent?
)

data class Timetables(
    val timetables: List<Timetable>
)

data class Timetable(
    val id: String?,
    val name: String?,
    val type: String?,
    val typeName: String?,
    val url: String?,
    val downloadUrl: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val selected: Boolean,
)

data class PeriodicContent(
    val events: List<Event>?,
    val recurrence: Recurrence?
)

data class NonPeriodicContent(
    val events: List<Event>?,
)

enum class TimetableType(val type: String?) {
    PERIODIC("PERIODIC"),
    NON_PERIODIC("NON_PERIODIC"),
    SESSION("SESSION")
}

data class NewsList(
    val maxPage: Int,
    val items: List<NewsShort>
)

data class NewsShort(
    val idInformation: Long,
    val title: String,
    val date: LocalDateTime,
    var thumbnail: String,
    val secondary: Secondary
)

data class Secondary(
    val text: String
)

data class News(
    val idInformation: Long,
    val title: String,
    val hisdateDisplay: String,
    val content: String,
    var elements: MutableList<Pair<String, Any>>?,
    var images: MutableList<String>?
)

data class Institutes(
    val institutes: List<Institute>?
)

data class Institute(
    val id: Int?,
    val name: String?,
    val abbreviation: String?,
    val courses: List<Course>?
)

data class Course(
    val course: String?,
    val specialties: List<Specialty>?
)

data class Specialty(
    val name: String?,
    val abbreviation: String?,
    val groups: List<Group>?
)

data class Group(
    val id: Int?,
    val name: String?,
    val url: String?,
)

data class Person(
    val name: String?,
    val id: Int?,
    val position: String?
)

data class Lecturer(
    val id: Int?,
    val shortFio: String?,
    val fullFio: String?,
    val description: String?,
    val url: String?,
    val hint: String?
)

data class Room(
    val id: Int?,
    val name: String?,
    val url: String?,
    val hint: String?
)

data class TelegramPage(
    val url: String?,
    val name: String?,
    val imageUrl: String?
)

data class RecurrenceRule(
    val frequency: String,
    val interval: Int
)