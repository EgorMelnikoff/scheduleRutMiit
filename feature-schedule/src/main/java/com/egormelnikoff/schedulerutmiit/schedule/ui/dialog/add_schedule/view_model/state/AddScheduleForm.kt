package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.schedule.data.validator.isValidSchedule
import java.time.LocalDate

data class AddScheduleForm(
    val name: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val timetableType: TimetableType = TimetableType.NON_PERIODIC
) {
    val isButtonEnabled: Boolean
        get() = isValidSchedule(
            name = name,
            start = startDate,
            end = endDate
        )
}