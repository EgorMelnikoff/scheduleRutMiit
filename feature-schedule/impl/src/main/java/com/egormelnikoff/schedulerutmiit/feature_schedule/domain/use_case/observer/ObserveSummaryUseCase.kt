package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.observer

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.SummaryState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleState
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveSummaryUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val namedScheduleDataSource: NamedScheduleDataSource
) {
    operator fun invoke() = namedScheduleDataSource.observeDefault().map { namedSchedule ->
        val schedule = namedSchedule
            ?.schedulesWithEvents
            ?.findDefault()
            ?: return@map null

        val scheduleState = ScheduleState.fromSchedule(schedule)

        SummaryState(
            schedule = scheduleState.schedule,
            periodicEvents = scheduleState.periodicEvents,
            nonPeriodicEvents = scheduleState.nonPeriodicEvents
        )
    }
}