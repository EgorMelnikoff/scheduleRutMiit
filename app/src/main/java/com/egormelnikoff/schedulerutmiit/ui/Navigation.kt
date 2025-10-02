package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.runtime.mutableStateListOf
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

class AppBackStack<T : Routes>(
    startRoute: T
) {
    val backStack = mutableStateListOf<Routes>(startRoute)

    fun last() = backStack.last ()

    fun lastPage() = backStack.last { !it.isDialog }

    fun onBack() {
        backStack.removeAt(backStack.lastIndex)
    }

    fun navigateToPage(route: Routes) {
        if (last().isDialog) {
            onBack()
        }
        backStack.removeAt(0)
        backStack.add(route)
    }

    fun navigateToDialog(dialog: Routes) {
        backStack.add(dialog)
    }
}