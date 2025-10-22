package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

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