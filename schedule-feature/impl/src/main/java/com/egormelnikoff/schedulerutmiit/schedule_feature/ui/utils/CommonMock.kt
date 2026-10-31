package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Group
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Lecturer
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Recurrence
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Room
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Schedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.TimetableType
import java.time.LocalDate
import java.time.LocalDateTime

val lecturerMock = Lecturer(
    fullFio = "Антонова Елена Вячеславовна",
    shortFio = "Антонова Е. В.",
    hint = ""
)

val groupMock = Group(
    id = 0,
    name = "УВП-211"
)
val roomMock = Room(
    name = "Аудитория 1333",
    hint = "ГУК-1, 1333"
)

val eventMock = Event(
    id = 0L,
    scheduleId = 0L,
    startDatetime = LocalDateTime.of(2026, 9, 1, 10, 5),
    endDatetime = LocalDateTime.of(2026, 9, 1, 11, 25),
    interval = 1,
    periodNumber = 1,
    name = "Математика",
    typeName = "Лекция",
    timeSlotName = "Пара 2",
    lecturers = listOf(
        lecturerMock
    ),
    groups = listOf(
        groupMock,
        groupMock
    ),
    rooms = listOf(
        roomMock
    ),
    isHidden = false,
    isCustomEvent = false
)

val namedScheduleMock = NamedSchedule(
    id = 0,
    fullName = "УВП-411",
    shortName = "УВП-411",
    apiId = null,
    type = NamedScheduleType.GROUP,
    isDefault = true,
    lastTimeUpdate = 0L
)

val periodicScheduleMock = Schedule(
    namedScheduleId = 0,
    timetableId = "",
    timetableType = TimetableType.PERIODIC,
    downloadUrl = null,
    startDate = LocalDate.of(2026, 9, 1),
    endDate = LocalDate.of(2027, 1, 4),
    recurrence = Recurrence(
        interval = 2,
        firstWeekNumber = 1
    ),
    isDefault = false
)
