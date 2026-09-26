package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import java.time.LocalDateTime

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