package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case.observer

import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.SummaryState
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.schedule.ScheduleState
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