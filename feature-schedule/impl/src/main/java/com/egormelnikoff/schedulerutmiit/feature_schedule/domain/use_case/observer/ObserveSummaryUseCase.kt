package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.observer

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.SummaryState
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ObserveSummaryUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos
) {
    operator fun invoke() = namedScheduleRepos.observeDefault().map { namedSchedule ->
        val scheduleWithEvents = namedSchedule
            ?.schedulesWithEvents
            ?.findDefault()
            ?: return@map null

        val schedule = scheduleWithEvents.schedule
        val visibleEvents = scheduleWithEvents.events.filter { !it.isHidden }

        val periodicEvents = schedule.recurrence?.let { recurrence ->
            visibleEvents.getPeriodicEvents(recurrence.interval)
        }

        val nonPeriodicEvents = if (periodicEvents == null) {
            visibleEvents.groupBy { it.startDatetime.toLocalDate() }
        } else {
            null
        }

        SummaryState(
            schedule = schedule,
            periodicEvents = periodicEvents,
            nonPeriodicEvents = nonPeriodicEvents
        )
    }
}
