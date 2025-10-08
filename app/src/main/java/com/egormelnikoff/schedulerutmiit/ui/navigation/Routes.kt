package com.egormelnikoff.schedulerutmiit.ui.navigation

import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity

sealed class Routes(val isDialog: Boolean) {
    data object Review : Routes(false)
    data object Schedule : Routes(false)
    data object NewsList : Routes(false)
    data object Settings : Routes(false)
    data class EventDialog(
        val event: Event,
        val eventExtraData: EventExtraData?
    ) : Routes(true)
    data object NewsDialog : Routes(true)
    data object InfoDialog : Routes(true)
    data class AddEventDialog(
        val scheduleEntity: ScheduleEntity
    ) : Routes(true)
    data object SearchDialog : Routes(true)
    data object AddScheduleDialog : Routes(true)
}