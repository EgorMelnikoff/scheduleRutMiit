package com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel

data class EventActions(
    val onAddCustomEvent: (Pair<ScheduleEntity, Event>) -> Unit,
    val onDeleteEvent: (Pair<ScheduleEntity, Long>) -> Unit, //EventPK
    val onHideEvent: (Pair<ScheduleEntity, Long>) -> Unit, //EventPK
    val onShowEvent: (Pair<ScheduleEntity, Long>) -> Unit, //EventPK
    val onEventExtraChange: (Triple<Event, String, Int>) -> Unit, //Event, Comment, Tag
) {
    companion object {
        fun getEventActions(
            scheduleViewModel: ScheduleViewModel
        ) = EventActions(
            onAddCustomEvent = { event ->
                scheduleViewModel.addCustomEvent(
                    scheduleEntity = event.first,
                    event = event.second
                )
            },
            onEventExtraChange = { value ->
                scheduleViewModel.updateEventExtra(
                    event = value.first,
                    comment = value.second,
                    tag = value.third
                )
            },
            onDeleteEvent = { event ->
                scheduleViewModel.deleteCustomEvent(
                    scheduleEntity = event.first,
                    eventPrimaryKey = event.second
                )

            },
            onShowEvent = { event ->
                scheduleViewModel.updateEventHidden(
                    scheduleEntity = event.first,
                    eventPrimaryKey = event.second,
                    isHidden = false
                )
            },
            onHideEvent = { event ->
                scheduleViewModel.updateEventHidden(
                    scheduleEntity = event.first,
                    eventPrimaryKey = event.second,
                    isHidden = true
                )
            }
        )
    }
}