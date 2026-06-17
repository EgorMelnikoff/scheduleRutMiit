package com.egormelnikoff.schedulerutmiit.core.database.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.database.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.RoomEntity
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(
    private val json: Json
) {
    @TypeConverter
    fun fromListLecturer(lecturers: List<LecturerEntity>?): String? {
        return lecturers?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListLecturer(lecturersString: String?): List<LecturerEntity>? {
        return lecturersString?.let { json.decodeFromString(it) }
    }


    @TypeConverter
    fun fromListRoom(rooms: List<RoomEntity>?): String? {
        return rooms?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListRoom(roomsString: String?): List<RoomEntity>? {
        return roomsString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListGroup(groups: List<GroupEntity>?): String? {
        return groups?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toListGroup(groupsString: String?): List<GroupEntity>? {
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


    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? =
        value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? =
        value?.let(LocalTime::parse)
}