package com.egormelnikoff.schedulerutmiit.app.validator

import android.content.Context
import com.egormelnikoff.schedulerutmiit.R
import java.time.LocalDate
import java.time.LocalTime

sealed class EventValidation {
    data object Success : EventValidation()
    data class Error(val message: String) : EventValidation()

    companion object {
        fun validate(
            context: Context,
            name: String,
            date: LocalDate?,
            startTime: LocalTime?,
            endTime: LocalTime?,
            roomsList: List<com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.RoomDto>,
            lecturersList: List<com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.LecturerDto>,
            groupsList: List<com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto>
        ): EventValidation {
            val errors = mutableListOf<String>()
            if (name.isBlank()) errors.add(context.getString(R.string.no_name_specified))
            if (date == null) errors.add(context.getString(R.string.no_date_specified))
            if (startTime == null) errors.add(context.getString(R.string.no_start_time_specified))
            if (endTime == null) errors.add(context.getString(R.string.no_end_time_specified))
            if (startTime != null && endTime != null && startTime > endTime) {
                errors.add(context.getString(R.string.time_is_chosen_incorrectly))
            }
            if (!roomsList.all { it.name.isNotEmpty() }) {
                errors.add("${context.getString(R.string.room_number_not_specified)}\n")
            }
            if (!lecturersList.all { it.fullFio.isNotEmpty() }) {
                errors.add("${context.getString(R.string.provide_the_names_of_all_lecturers)}\n")
            }
            if (!groupsList.all { it.name.isNotEmpty() }) {
                errors.add(context.getString(R.string.provide_the_numbers_of_all_groups))
            }

            return if (errors.isEmpty()) {
                Success
            } else {
                Error(errors.joinToString("\n"))
            }
        }
    }
}