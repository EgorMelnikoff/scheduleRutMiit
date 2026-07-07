package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.state.EventState
import java.time.LocalDate
import javax.inject.Inject

class LoadEventUseCase @Inject constructor(
    private val eventRepos: EventRepos,
    private val eventExtraRepos: EventExtraRepos,
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(eventId: Long, date: LocalDate?): EventState.Loaded {
        val event = eventRepos.getById(eventId)
        val eventExtraData = eventExtraRepos.get(eventId, date)

        return EventState.Loaded(
            event = event,
            schedule = scheduleRepos.getById(event.scheduleId),
            comment = eventExtraData?.comment ?: "",
            tag = eventExtraData?.tag ?: 0
        )
    }
}