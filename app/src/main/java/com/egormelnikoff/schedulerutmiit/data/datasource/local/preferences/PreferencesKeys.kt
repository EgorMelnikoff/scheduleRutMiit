package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val THEME = stringPreferencesKey(name = "theme")
    val DECOR_COLOR = intPreferencesKey(name = "decor_color")
    val SCHEDULE_VIEW = booleanPreferencesKey(name = "schedule_view")
    val SHOW_COUNT_CLASSES = booleanPreferencesKey(name = "show_count_classes")

    val EVENT_GROUPS_VISIBILITY = booleanPreferencesKey(name = "event_groups_visibility")
    val EVENT_ROOMS_VISIBILITY = booleanPreferencesKey(name = "event_rooms_visibility")
    val EVENT_LECTURERS_VISIBILITY = booleanPreferencesKey(name = "event_lecturers_visibility")
    val EVENT_TAG_VISIBILITY = booleanPreferencesKey(name = "event_tag_visibility")
    val EVENT_COMMENT_VISIBILITY = booleanPreferencesKey(name = "event_comment_visibility")

    val SCHEDULE_UPDATE = booleanPreferencesKey(name = "schedule_update")
    val WIDGET_UPDATE = booleanPreferencesKey(name = "widget_update")
}