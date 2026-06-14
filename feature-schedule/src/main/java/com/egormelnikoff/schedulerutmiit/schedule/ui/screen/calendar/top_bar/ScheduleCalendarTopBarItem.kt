package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.calendar.top_bar

import androidx.compose.runtime.Composable
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomBadge
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.top_bar.CalendarTopBarItem
import java.time.LocalDate

@Composable
fun ScheduleCalendarTopBarItem(
    selectDate: (LocalDate) -> Unit,
    currentDate: LocalDate,
    isDisabled: Boolean,
    isSelected: Boolean,
    eventsCountView: EventsCountView,
    isToday: Boolean,
    events: Map<String, List<Event>>,
    eventsExtraData: Map<Long, EventExtraData>,
    eventExtraPolicy: EventExtraPolicy,
) {
    CalendarTopBarItem(
        selectDate = selectDate,
        currentDate = currentDate,
        isDisabled = isDisabled,
        isSelected = isSelected,
        isToday = isToday
    ) {
        if (eventsCountView == EventsCountView.DETAILS) {
            EventsDetailBadge(
                currentDate = currentDate,
                events = events,
                eventsExtraData = eventsExtraData,
                eventExtraPolicy = eventExtraPolicy
            )
        } else if (eventsCountView == EventsCountView.BRIEFLY && events.isNotEmpty()) {
            CustomBadge(
                count = events.size
            )
        }
    }
}
