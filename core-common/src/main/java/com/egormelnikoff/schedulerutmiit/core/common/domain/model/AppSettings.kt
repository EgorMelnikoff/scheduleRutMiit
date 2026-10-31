package com.egormelnikoff.schedulerutmiit.core.common.domain.model

import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.ScheduleView

data class AppSettings(
    val themePreferences: ThemePreferences,
    val scheduleView: ScheduleView,
    val eventView: EventView,
    val eventsCountView: EventsCountView,
    val schedulesDeletable: Boolean,
    val usedImageInReview: Boolean,
    val skipWelcomePage: Boolean,
    val eventExtraPolicy: EventExtraPolicy
)