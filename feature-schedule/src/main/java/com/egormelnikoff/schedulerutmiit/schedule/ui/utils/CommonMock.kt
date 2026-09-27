package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.enums.TimetableType
import java.time.LocalDate
import java.time.LocalDateTime

val lecturerMock = Lecturer(
    fullFio = "Антонова Елена Вячеславовна",
    shortFio = "Антонова Е. В."
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
    )
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
    )
)
