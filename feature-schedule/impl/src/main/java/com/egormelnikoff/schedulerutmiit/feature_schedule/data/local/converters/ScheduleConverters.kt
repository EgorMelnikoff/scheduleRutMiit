package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.RoomEntity
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@ProvidedTypeConverter
class ScheduleConverters @Inject constructor(
    private val json: Json
) {
    @TypeConverter
    fun fromListLecturer(lecturers: List<LecturerEntity>?): String? =
        lecturers?.let { json.encodeToString(it) }

    @TypeConverter
    fun fromListRoom(rooms: List<RoomEntity>?): String? =
        rooms?.let { json.encodeToString(it) }

    @TypeConverter
    fun fromListGroup(groups: List<GroupEntity>?): String? =
        groups?.let { json.encodeToString(it) }



    @TypeConverter
    fun toListLecturer(lecturersString: String?): List<LecturerEntity>? =
        lecturersString?.let { json.decodeFromString(it) }

    @TypeConverter
    fun toListRoom(roomsString: String?): List<RoomEntity>? =
        roomsString?.let { json.decodeFromString(it) }

    @TypeConverter
    fun toListGroup(groupsString: String?): List<GroupEntity>? =
        groupsString?.let { json.decodeFromString(it) }



    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()


    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)



    @TypeConverter
    fun fromNamedScheduleType(type: NamedScheduleType) = type.ordinal

    @TypeConverter
    fun toNamedScheduleType(value: Int) = NamedScheduleType.entries[value]

    @TypeConverter
    fun fromTimetableType(type: TimetableType): Int = type.ordinal

    @TypeConverter
    fun toTimetableType(value: Int): TimetableType = TimetableType.entries[value]
}