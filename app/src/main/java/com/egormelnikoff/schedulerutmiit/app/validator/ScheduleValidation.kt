package com.egormelnikoff.schedulerutmiit.app.validator

import android.content.Context
import com.egormelnikoff.schedulerutmiit.R
import java.time.LocalDate

sealed class ScheduleValidation {
    data class Success(val startDate: LocalDate, val endDate: LocalDate) : ScheduleValidation()
    data class Error(val message: String) : ScheduleValidation()

    companion object {
        fun validate(
            context: Context,
            name: String,
            start: LocalDate?,
            end: LocalDate?
        ): ScheduleValidation {
            val errors = mutableListOf<String>()
            if (name.isBlank()) errors.add(context.getString(R.string.no_name_specified))
            if (start == null) errors.add(context.getString(R.string.no_start_date_specified))
            if (end == null) errors.add(context.getString(R.string.no_end_date_specified))

            return if (errors.isEmpty() && start != null && end != null) {
                Success(start, end)
            } else {
                Error(errors.joinToString("\n"))
            }
        }
    }
}