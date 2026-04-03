package com.egormelnikoff.schedulerutmiit.app.network.model

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.Lecturer
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.RecurrenceRule
import com.egormelnikoff.schedulerutmiit.app.entity.Room
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
data class ScheduleModel(
    val timetable: TimetableModel,
    val periodicContent: PeriodicContentModel?,
    val nonPeriodicContent: NonPeriodicContentModel?
)

@Keep
data class TimetablesModel(
    val timetables: List<TimetableModel>
)

@Keep
data class TimetableModel(
    val id: String,
    val name: String,
    val type: TimetableType,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate
)

@Keep
data class PeriodicContentModel(
    val events: List<EventModel>?,
    val recurrence: Recurrence
)

@Keep
data class NonPeriodicContentModel(
    val events: List<EventModel>?,
)

@Keep
data class EventModel(
    val startDatetime: LocalDateTime?,
    val endDatetime: LocalDateTime?,
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
        Event(
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
data class InstitutesModel(
    val instituteModels: List<InstituteModel>
)

@Keep
data class InstituteModel(
    val id: Int,
    val name: String,
    val abbreviation: String,
    val courses: List<CourseModel>
)

@Keep
data class CourseModel(
    val course: String,
    val specialties: List<SpecialtyModel>
)

@Keep
data class SpecialtyModel(
    val name: String,
    val abbreviation: String,
    val groups: List<Group>
)

@Keep
data class PersonModel(
    val name: String,
    val id: Int,
    val position: String
)


@Keep
data class NewsListModel(
    val maxPage: Int,
    val items: List<NewsShortModel>
)

@Keep
data class NewsShortModel(
    val idInformation: Long,
    val title: String,
    val date: LocalDateTime,
    val thumbnail: String,
    val secondary: SecondaryModel
)

@Keep
data class SecondaryModel(
    val text: String
)

@Keep
data class NewsModel(
    val idInformation: Long,
    val title: String,
    val hisdateDisplay: String,
    val content: String
)

@Keep
data class NewsContent(
    val newsModel: NewsModel,
    val elements: MutableList<Pair<String, Any>>,
    val images: MutableList<String>
)

@Keep
data class SubjectModel(
    val title: String,
    val teachers: Set<String>
)