package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.DayPeriod
import java.time.LocalDate

internal data class ReviewState(
    val currentDate: LocalDate? = null,
    val dayPeriod: DayPeriod = DayPeriod.DAY,
    val summaryState: SummaryState? = null,
    val namedSchedules: List<NamedSchedule> = listOf(),

    val showSchedules: Boolean = true,
    val showServices: Boolean = true,
    val scheduleSheet: NamedSchedule? = null,
    val scheduleDeleteSheet: NamedSchedule? = null,
)