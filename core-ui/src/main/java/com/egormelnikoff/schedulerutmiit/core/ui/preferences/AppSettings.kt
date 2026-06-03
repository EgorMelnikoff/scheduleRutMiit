package com.egormelnikoff.schedulerutmiit.core.ui.preferences

import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView

data class AppSettings(
    val decorPreferences: DecorPreferences,
    val scheduleView: ScheduleView,
    val eventView: EventView,
    val eventsCountView: EventsCountView,
    val schedulesDeletable: Boolean,
    val usedImageInReview: Boolean,
    val skipWelcomePage: Boolean,
    val eventExtraPolicy: EventExtraPolicy
)