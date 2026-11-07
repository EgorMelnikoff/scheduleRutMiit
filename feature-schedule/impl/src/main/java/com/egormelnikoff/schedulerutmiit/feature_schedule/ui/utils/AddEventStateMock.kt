package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Room
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.EditForm

val addEventStateMock = AddEventState.Loaded(
    schedule = periodicScheduleMock,
    editForm = EditForm(
        isEdit = false,
        rooms = listOf(
            Room(
                name = "Аудитория 1333",
                hint = ""
            )
        )
    )
)