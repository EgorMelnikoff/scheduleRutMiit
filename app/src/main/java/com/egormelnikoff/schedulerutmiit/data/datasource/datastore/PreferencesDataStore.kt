package com.egormelnikoff.schedulerutmiit.data.datasource.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

data class AppSettings(
    val theme: String,
    val decorColorIndex: Int,
    val eventView: Boolean,
    val calendarView: Boolean,
    val showCountClasses: Boolean,
)

object PreferencesKeys {
    val THEME = stringPreferencesKey(name = "theme")
    val DECOR_COLOR = intPreferencesKey(name = "decor_color")
    val COMPACT_VIEW_EVENT = booleanPreferencesKey(name = "event_view")

    val SCHEDULE_VIEW = booleanPreferencesKey(name = "schedule_view")
    val SHOW_COUNT_CLASSES = booleanPreferencesKey(name = "show_count_classes")
}

class PreferencesDataStore @Inject constructor(
    private val context: Context
) {
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    suspend fun setDecorColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DECOR_COLOR] = color
        }
    }

    suspend fun setScheduleView(isCalendar: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULE_VIEW] = isCalendar
        }
    }

    suspend fun setViewEvent(isShort: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COMPACT_VIEW_EVENT] = isShort
        }
    }


    suspend fun setShowCountClasses(isShowCountClasses: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_COUNT_CLASSES] = isShowCountClasses
        }
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME] ?: "system"
    }

    val decorColorFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DECOR_COLOR] ?: 0
    }

    val viewEventFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COMPACT_VIEW_EVENT] ?: false
    }

    val scheduleViewFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SCHEDULE_VIEW] ?: true
    }

    val showCountClassesFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHOW_COUNT_CLASSES] ?: true
    }
}