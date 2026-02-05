package com.egormelnikoff.schedulerutmiit.data.datasource.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.Lecturer
import com.egormelnikoff.schedulerutmiit.app.entity.Room
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.TimetableType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(
    private val gson: Gson
) {
    @TypeConverter
    fun fromListLecturer(lecturers: List<Lecturer>?): String? {
        return lecturers?.let {
            gson.toJson(lecturers)
        }
    }

    @TypeConverter
    fun toListLecturer(lecturersString: String?): List<Lecturer>? {
        return lecturersString?.let {
            val type = object : TypeToken<List<Lecturer>?>() {}.type
            gson.fromJson(lecturersString, type)
        }
    }

    @TypeConverter
    fun fromListRoom(rooms: List<Room>?): String? {
        return rooms?.let {
            return gson.toJson(rooms)
        }
    }

    @TypeConverter
    fun toListRoom(roomsString: String?): List<Room>? {
        return roomsString?.let {
            val type = object : TypeToken<List<Room>?>() {}.type
            gson.fromJson(roomsString, type)
        }
    }

    @TypeConverter
    fun fromListGroup(groups: List<Group>?): String? {
        return groups?.let {
            gson.toJson(groups)
        }
    }

    @TypeConverter
    fun toListGroup(groupsString: String?): List<Group>? {
        return groupsString?.let {
            val type = object : TypeToken<List<Group>?>() {}.type
            gson.fromJson(groupsString, type)
        }
    }


    @TypeConverter
    fun toLocalDateString(localDate: LocalDate?): String? {
        return localDate?.let {
            localDate.format(DateTimeFormatter.ISO_DATE)
        }
    }

    @TypeConverter
    fun toLocalDate(localDateString: String?): LocalDate? {
        return localDateString?.let {
            LocalDate.parse(localDateString, DateTimeFormatter.ISO_DATE)
        }
    }

    @TypeConverter
    fun toLocalDateTimeString(localDateTime: LocalDateTime?): String? {
        return localDateTime?.let {
            localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    @TypeConverter
    fun toLocalDateTime(localDateTimeString: String?): LocalDateTime? {
        return localDateTimeString?.let {
            LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    @TypeConverter
    fun fromNamedScheduleType(type: NamedScheduleType) = type.id

    @TypeConverter
    fun toNamedScheduleType(value: Int): NamedScheduleType {
        return when (value) {
            0 -> NamedScheduleType.Group
            1 -> NamedScheduleType.Person
            2 -> NamedScheduleType.Room
            else -> NamedScheduleType.My
        }
    }

    @TypeConverter
    fun fromTimetableType(type: TimetableType): Int =
        type.ordinal

    @TypeConverter
    fun toTimetableType(value: Int): TimetableType =
        TimetableType.entries[value]
}