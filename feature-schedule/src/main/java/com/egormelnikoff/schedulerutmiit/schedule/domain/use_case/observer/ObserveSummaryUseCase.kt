package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer

import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findDefault
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review.view_model.state.SummaryState
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state.ScheduleState
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveSummaryUseCase @Inject constructor(
    private val observeDefaultNamedScheduleUseCase: ObserveDefaultNamedScheduleUseCase
) {
    operator fun invoke() = observeDefaultNamedScheduleUseCase().map { namedSchedule ->
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