package com.egormelnikoff.schedulerutmiit.schedule.data.validator

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import java.time.LocalDate
import java.time.LocalTime

fun isValidSchedule(
    name: String,
    start: LocalDate?,
    end: LocalDate?
): Boolean {
    if (name.isBlank()) return false
    if (start == null) return false
    if (end == null) return false
    return true
}

fun isValidEvent(
    name: String,
    date: LocalDate?,
    startTime: LocalTime?,
    endTime: LocalTime?,
    roomsList: List<Room>,
    lecturersList: List<Lecturer>,
    groupsList: List<Group>
): Boolean {
    if (name.isBlank()) return false
    if (date == null) return false
    if (startTime == null) return false
    if (endTime == null) return false
    if (startTime > endTime) return false
    if (!roomsList.all { it.name.isNotBlank() }) return false
    if (!lecturersList.all { it.fullFio.isNotBlank() }) return false
    if (!groupsList.all { it.name.isNotBlank() }) return false
    return true
}