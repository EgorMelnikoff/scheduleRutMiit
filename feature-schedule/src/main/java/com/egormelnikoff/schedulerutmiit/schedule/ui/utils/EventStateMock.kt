package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState


val eventStateMock = EventScreenState.Loaded(
    event = eventMock,
    schedule = periodicScheduleMock,
    comment = "Тестовый комментарий",
    tag = 1
)