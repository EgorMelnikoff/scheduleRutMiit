package com.egormelnikoff.schedulerutmiit.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val THEME = stringPreferencesKey(name = "theme")
    val USED_AMOLED = booleanPreferencesKey(name = "used_amoled")
    val DECOR_COLOR = intPreferencesKey(name = "decor_color")
    val SCHEDULE_VIEW = stringPreferencesKey(name = "view_schedule")
    val SCHEDULES_DELETABLE = booleanPreferencesKey(name = "schedules_deletable")
    val COUNT_CLASSES_VIEW = stringPreferencesKey(name = "count_classes_view")

    val SKIP_WELCOME_PAGE = booleanPreferencesKey(name = "skip_welcome_page")

    val EVENT_EXTRA_POLICY = stringPreferencesKey("event_extra_policy")

    val LATEST_RELEASE = stringPreferencesKey(name = "latest_release")
    val EVENT_GROUPS_VISIBILITY = booleanPreferencesKey(name = "event_groups_visibility")
    val EVENT_ROOMS_VISIBILITY = booleanPreferencesKey(name = "event_rooms_visibility")
    val EVENT_LECTURERS_VISIBILITY = booleanPreferencesKey(name = "event_lecturers_visibility")
    val EVENT_TAG_VISIBILITY = booleanPreferencesKey(name = "event_tag_visibility")
    val EVENT_COMMENT_VISIBILITY = booleanPreferencesKey(name = "event_comment_visibility")
}