package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState

val eventStateMock = EventState.Loaded(
    event = eventMock,
    schedule = periodicScheduleMock,
    comment = "Тестовый комментарий",
    tag = 1
)