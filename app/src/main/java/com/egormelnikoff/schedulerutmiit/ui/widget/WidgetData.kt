package com.egormelnikoff.schedulerutmiit.ui.widget

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.extension.getPeriodicEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.database.entity.Event
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.Schedule
import com.egormelnikoff.schedulerutmiit.ui.view_models.schedule.state.ui_dto.ReviewUiDto
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Serializable
@Keep
data class WidgetData(
    val namedScheduleEntity: NamedScheduleEntity? = null,
    val settledScheduleEntity: ScheduleEntity? = null,
    val reviewUiDto: ReviewUiDto? = null,
    val eventsExtraData: List<EventExtraData> = listOf(),
    val eventExtraPolicy: EventExtraPolicy = EventExtraPolicy.DEFAULT
) {
    companion object {
        operator fun invoke(
            namedScheduleEntity: NamedScheduleEntity?,
            schedule: Schedule?,
            eventExtraPolicy: EventExtraPolicy
        ): WidgetData? {
            return if (schedule != null) {
                val splitEvents = schedule.events.partition { it.isHidden }

                var periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>? = null
                var nonPeriodicEvents: Map<LocalDate, List<Event>>? = null

                if (schedule.scheduleEntity.recurrence != null) {
                    periodicEvents = splitEvents.second.getPeriodicEvents(
                        requireNotNull(schedule.scheduleEntity.recurrence).interval,
                    )
                } else {
                    nonPeriodicEvents = splitEvents.second.groupBy {
                        it.startDatetime.toLocalDate()
                    }
                }
                WidgetData(
                    namedScheduleEntity = namedScheduleEntity,
                    settledScheduleEntity = schedule.scheduleEntity,
                    eventsExtraData = schedule.eventsExtraData,
                    reviewUiDto = ReviewUiDto(
                        scheduleEntity = schedule.scheduleEntity,
                        periodicEvents = periodicEvents,
                        nonPeriodicEvents = nonPeriodicEvents
                    ),
                    eventExtraPolicy = eventExtraPolicy
                )
            } else null
        }
    }
}