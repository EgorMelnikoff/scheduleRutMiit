package com.egormelnikoff.schedulerutmiit.ui.state.actions

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel

data class EventActions(
    val onAddCustomEvent: (ScheduleEntity, Event) -> Unit,
    val onUpdateCustomEvent: (ScheduleEntity, Event) -> Unit,
    val onDeleteEvent: (ScheduleEntity, Long) -> Unit, //EventPK
    val onHideEvent: (ScheduleEntity, Long) -> Unit, //EventPK
    val onShowEvent: (ScheduleEntity, Long) -> Unit, //EventPK
    val onEventExtraChange: (Event, String, Int) -> Unit, //Event, Comment, Tag
) {
    companion object {
        operator fun invoke(
            scheduleViewModel: ScheduleViewModel
        ) = EventActions(
            onUpdateCustomEvent = { scheduleEntity, event ->
                scheduleViewModel.updateCustomEvent(scheduleEntity, event)
            },
            onAddCustomEvent = { scheduleEntity, event ->
                scheduleViewModel.addCustomEvent(
                    scheduleEntity = scheduleEntity,
                    event = event
                )
            },
            onEventExtraChange = { event, comment, tag ->
                scheduleViewModel.updateEventExtra(
                    event = event,
                    comment = comment,
                    tag = tag
                )
            },
            onDeleteEvent = { scheduleEntity, event->
                scheduleViewModel.deleteCustomEvent(
                    scheduleEntity = scheduleEntity,
                    eventPrimaryKey = event
                )

            },
            onShowEvent = { scheduleEntity, event ->
                scheduleViewModel.updateEventHidden(
                    scheduleEntity = scheduleEntity,
                    eventPrimaryKey = event,
                    isHidden = false
                )
            },
            onHideEvent = { scheduleEntity, eventPrimaryKey ->
                scheduleViewModel.updateEventHidden(
                    scheduleEntity = scheduleEntity,
                    eventPrimaryKey = eventPrimaryKey,
                    isHidden = true
                )
            }
        )
    }
}