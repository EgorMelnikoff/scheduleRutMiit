package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val THEME = stringPreferencesKey(name = "theme")
    val DECOR_COLOR = intPreferencesKey(name = "decor_color")
    val COMPACT_VIEW_EVENT = booleanPreferencesKey(name = "event_view")

    val SCHEDULE_VIEW = booleanPreferencesKey(name = "schedule_view")
    val SHOW_COUNT_CLASSES = booleanPreferencesKey(name = "show_count_classes")
}