package com.egormelnikoff.schedulerutmiit.app.extension

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.Schedule
import java.time.Duration
import java.time.LocalDateTime

fun List<Schedule>.findDefaultSchedule(): Schedule? {
    return this.find { it.scheduleEntity.isDefault } ?: this.firstOrNull()
}

fun String.getShortName(type: NamedScheduleType): String {
    if (type != NamedScheduleType.PERSON) return this
    val nameParts = this.split(" ")
    return if (nameParts.size == 3) {
        "${nameParts.first()} ${nameParts[1][0]}. ${nameParts[2][0]}."
    } else this
}

fun getTimeSlotName(
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?
): String? {
    if (Duration.between(startDateTime, endDateTime).toMinutes() != 80L) {
        return null
    }

    return when (startDateTime?.hour) {
        5 if startDateTime.minute == 30 -> "1 пара"
        7 if startDateTime.minute == 5 -> "2 пара"
        8 if startDateTime.minute == 40 -> "3 пара"
        10 if startDateTime.minute == 45 -> "4 пара"
        12 if startDateTime.minute == 20 -> "5 пара"
        13 if startDateTime.minute == 55 -> "6 пара"
        15 if startDateTime.minute == 30 -> "7 пара"
        17 if startDateTime.minute == 0 -> "8 пара"
        18 if startDateTime.minute == 35 -> "9 пара"
        20 if startDateTime.minute == 10 -> "10 пара"
        else -> null
    }
}