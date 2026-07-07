package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.domain.DefaultEventParams
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.schedule.data.validator.isValidEvent
import java.time.LocalDate
import java.time.LocalTime

data class EditEventForm(
    val isEdit: Boolean,
    val name: String = "",
    val type: String? = DefaultEventParams.types.first(),

    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val interval: Int = -1,
    val period: Int = 1,

    val rooms: List<Room> = emptyList(),
    val lecturers: List<Lecturer> = emptyList(),
    val groups: List<Group> = emptyList()
) {

    val isValid: Boolean
        get() = isValidEvent(name, date, startTime, endTime, rooms, lecturers, groups)

    companion object {
        operator fun invoke(event: Event?) = event?.let {
            EditEventForm(
                isEdit = true,
                name = event.name,
                type = event.typeName,
                date = event.startDatetime.toLocalDate(),
                startTime = event.startDatetime.toLocalTime(),
                endTime = event.endDatetime.toLocalTime(),

                interval = event.interval ?: -1,
                period = event.periodNumber ?: -1,

                rooms = event.rooms ?: emptyList(),
                lecturers = event.lecturers ?: emptyList(),
                groups = event.groups ?: emptyList()
            )
        } ?: EditEventForm(false)
    }
}