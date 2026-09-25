package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import java.time.LocalDate
import java.time.LocalDateTime


val eventStateMock = EventScreenState.Loaded(
    event = Event(
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
            Lecturer(
                fullFio = "Антонова Елена Вячеславовна",
                shortFio = "Антонова Е. В."
            )
        ),
        groups = listOf(
            Group(
                id = 0,
                name = "УВП-211"
            ),
            Group(
                id = 0,
                name = "УВВ-211"
            )
        ),
        rooms = listOf(
            Room(
                name = "Аудитория 1333",
                hint = "ГУК-1, 1333"
            )
        )
    ),
    schedule = Schedule(
        namedScheduleId = 0,
        timetableId = "",
        timetableType = TimetableType.PERIODIC,
        downloadUrl = null,
        startDate = LocalDate.of(2026, 9, 1),
        endDate =  LocalDate.of(2027, 1, 4),
        recurrence = null
    ),
    comment = "",
    tag = 1
)