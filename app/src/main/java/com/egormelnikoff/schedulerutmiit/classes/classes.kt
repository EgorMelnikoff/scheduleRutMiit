package com.egormelnikoff.schedulerutmiit.classes

import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.Group
import com.egormelnikoff.schedulerutmiit.data.Recurrence
import java.time.LocalDate
import java.time.LocalDateTime

//Schedule
data class Schedule (
    val timetable: Timetable?,
    val periodicContent: PeriodicContent?,
    val nonPeriodicContent: NonPeriodicContent?
)

data class Timetables (
    val timetables: List<Timetable>
)

data class Timetable (
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

data class PeriodicContent (
    val events: List<Event>?,
    val recurrence: Recurrence?
)

data class NonPeriodicContent (
    val events: List<Event>?,
)

enum class TimetableType(val type: String?) {
    PERIODIC("PERIODIC"),
    NON_PERIODIC("NON_PERIODIC"),
    SESSION("SESSION")
}

//News
data class NewsList (
    val items: List<NewsShort>
)

data class NewsShort (
    val idInformation: Long,
    val title: String,
    val date: LocalDateTime,
    var thumbnail: String,
    val secondary: Secondary
)
data class Secondary (
    val text: String
)

data class News (
    val idInformation: Long,
    val title: String,
    val hisdateDisplay: String,
    val content: String,
    var elements: MutableList<Pair<String, Any>>?,
    var images: MutableList<String>?
)

//Institutes
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

//Person
data class Person (
    val name: String?,
    val id: Int?,
    val position: String?
)

//TelegramPage
data class TelegramPage (
    val url: String?,
    val name: String?,
    val imageUrl: String?
)

