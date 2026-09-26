package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule

import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.schedule.data.validator.isValidSchedule
import java.time.LocalDate

data class AddScheduleState(
    val name: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val timetableType: TimetableType = TimetableType.NON_PERIODIC,

    val showBackDialog: Boolean = false,
    val showDialogDate: Boolean = false
) {
    val isButtonEnabled: Boolean
        get() = isValidSchedule(
            name = name,
            start = startDate,
            end = endDate
        )
}