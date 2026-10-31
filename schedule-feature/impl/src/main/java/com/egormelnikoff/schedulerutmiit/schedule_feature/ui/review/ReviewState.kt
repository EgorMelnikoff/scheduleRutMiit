package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review

import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.Theme
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.DayPeriod
import java.time.LocalDate

data class ReviewState(
    val currentDate: LocalDate? = null,
    val dayPeriod: DayPeriod = DayPeriod.DAY,
    val summaryState: SummaryState? = null,
    val namedSchedules: List<NamedSchedule> = listOf(),


    val theme: Theme = Theme.DARK,
    val usedPhoto: Boolean = true,
    val showSchedules: Boolean = true,
    val showServices: Boolean = true,
    val scheduleSheet: NamedSchedule? = null,
    val scheduleDeleteSheet: NamedSchedule? = null,
)