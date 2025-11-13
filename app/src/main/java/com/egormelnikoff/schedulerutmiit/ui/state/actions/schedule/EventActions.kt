package com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel

data class EventActions(
    val onAddCustomEvent: (Pair<ScheduleEntity, Event>) -> Unit,
    val onDeleteEvent: (Long) -> Unit, //EventPK
    val onHideEvent: (Long) -> Unit, //EventPK
    val onShowEvent: (Long) -> Unit, //EventPK
    val onEventExtraChange: (Triple<Event, String, Int>) -> Unit, //Event, Comment, Tag
) {
    companion object {
        fun getEventActions(
            scheduleViewModel: ScheduleViewModel,
            scheduleState: ScheduleState,
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
            onDeleteEvent = { primaryKey ->
                scheduleViewModel.deleteCustomEvent(
                    scheduleEntity = scheduleState.currentNamedScheduleData!!.settledScheduleEntity!!,
                    eventPrimaryKey = primaryKey
                )
            },
            onShowEvent = { primaryKey ->
                scheduleViewModel.updateEventHidden(
                    scheduleEntity = scheduleState.currentNamedScheduleData!!.settledScheduleEntity!!,
                    eventPrimaryKey = primaryKey,
                    isHidden = false
                )
            },
            onHideEvent = { primaryKey ->
                scheduleViewModel.updateEventHidden(
                    scheduleEntity = scheduleState.currentNamedScheduleData!!.settledScheduleEntity!!,
                    eventPrimaryKey = primaryKey,
                    isHidden = true
                )
            }
        )
    }
}