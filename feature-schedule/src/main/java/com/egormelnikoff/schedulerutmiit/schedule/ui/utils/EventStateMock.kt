package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState
import java.time.LocalDate
import java.time.LocalDateTime


val eventStateMock = EventScreenState.Loaded(
    event = eventMock,
    schedule = periodicScheduleMock,
    comment = "Тестовый комментарий",
    tag = 1
)