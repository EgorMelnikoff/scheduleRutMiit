package com.egormelnikoff.schedulerutmiit.data.repos.local.database

import androidx.room.TypeConverter
import com.egormelnikoff.schedulerutmiit.data.Group
import com.egormelnikoff.schedulerutmiit.data.Lecturer
import com.egormelnikoff.schedulerutmiit.data.Room
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val formattedDateTime = src?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return JsonPrimitive(formattedDateTime)
    }
}

class Converters {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
        .registerTypeAdapter(
            LocalDateTime::class.java,
            JsonDeserializer { json, _, _ ->
                LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
        ).create()


    @TypeConverter
    fun fromListLecturer(lecturers: List<Lecturer>?): String? {
        if (lecturers == null) {
            return null
        }
        return gson.toJson(lecturers)
    }

    @TypeConverter
    fun toListLecturer(lecturersString: String?): List<Lecturer>? {
        if (lecturersString == null) {
            return null
        }
        val type = object : TypeToken<List<Lecturer>?>() {}.type
        return gson.fromJson(lecturersString, type)
    }

    @TypeConverter
    fun fromListRoom(rooms: List<Room>?): String? {
        if (rooms == null) {
            return null
        }
        return gson.toJson(rooms)
    }

    @TypeConverter
    fun toListRoom(roomsString: String?): List<Room>? {
        if (roomsString == null) {
            return null
        }
        val type = object : TypeToken<List<Room>?>() {}.type
        return gson.fromJson(roomsString, type)
    }

    @TypeConverter
    fun fromListGroup(groups: List<Group>?): String? {
        if (groups == null) {
            return null
        }
        return gson.toJson(groups)
    }

    @TypeConverter
    fun toListGroup(groupsString: String?): List<Group>? {
        if (groupsString == null) {
            return null
        }
        val type = object : TypeToken<List<Group>?>() {}.type
        return gson.fromJson(groupsString, type)
    }


    @TypeConverter
    fun toLocalDateString (localDate: LocalDate?): String? {
        if (localDate != null) {
            return localDate.format(DateTimeFormatter.ISO_DATE)
        }
        return null
    }

    @TypeConverter
    fun toLocalDate (localDateString: String?): LocalDate? {
        if (localDateString != null) {
            return LocalDate.parse(localDateString, DateTimeFormatter.ISO_DATE)
        }
        return null
    }

    @TypeConverter
    fun toLocalDateTimeString (localDateTime: LocalDateTime?): String? {
        if (localDateTime != null) {
            return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
        return null
    }

    @TypeConverter
    fun toLocalDateTime (localDateTimeString: String?): LocalDateTime? {
        if (localDateTimeString != null) {
            return LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
        return null
    }
}