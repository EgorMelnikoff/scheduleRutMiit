package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils

import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event.EventState

val eventStateMock = EventState.Loaded(
    event = eventMock,
    schedule = periodicScheduleMock,
    comment = "Тестовый комментарий",
    tag = 1
)