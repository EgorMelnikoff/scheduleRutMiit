package com.egormelnikoff.schedulerutmiit.core.settings.api.model

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