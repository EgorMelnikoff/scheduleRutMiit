package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.validator.isValidSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
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