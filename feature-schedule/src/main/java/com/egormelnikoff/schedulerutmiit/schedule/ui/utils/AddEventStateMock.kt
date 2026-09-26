package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.EditForm

val addEventStateMock = AddEventState.Loaded(
    schedule = periodicScheduleMock,
    editForm = EditForm(
        isEdit = false,
        rooms = listOf(
            Room(
                name = "Аудитория 1333"
            )
        )
    )
)