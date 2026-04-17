package com.egormelnikoff.schedulerutmiit.data.local.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(
    private val json: Json
) {
    @TypeConverter
    fun fromListLecturer(lecturers: List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.LecturerDto>?): String? {
        return lecturers?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListLecturer(lecturersString: String?): List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.LecturerDto>? {
        return lecturersString?.let { json.decodeFromString(it) }
    }


    @TypeConverter
    fun fromListRoom(rooms: List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.RoomDto>?): String? {
        return rooms?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListRoom(roomsString: String?): List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.RoomDto>? {
        return roomsString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListGroup(groups: List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto>?): String? {
        return groups?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListGroup(groupsString: String?): List<com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.event.GroupDto>? {
        return groupsString?.let { json.decodeFromString(it) }
    }


    @TypeConverter
    fun toLocalDateString(localDate: LocalDate?): String? = localDate?.toString()

    @TypeConverter
    fun toLocalDate(localDateString: String?): LocalDate? =
        localDateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun toLocalDateTimeString(localDateTime: LocalDateTime?): String? = localDateTime?.toString()

    @TypeConverter
    fun toLocalDateTime(localDateTimeString: String?): LocalDateTime? =
        localDateTimeString?.let { LocalDateTime.parse(it) }


    @TypeConverter
    fun fromNamedScheduleType(type: NamedScheduleType) = type.ordinal

    @TypeConverter
    fun toNamedScheduleType(value: Int) = NamedScheduleType.entries[value]

    @TypeConverter
    fun fromTimetableType(type: TimetableType): Int = type.ordinal

    @TypeConverter
    fun toTimetableType(value: Int): TimetableType = TimetableType.entries[value]
}