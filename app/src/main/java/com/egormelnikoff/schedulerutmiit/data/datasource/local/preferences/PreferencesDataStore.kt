package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences

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

    suspend fun setSchedulesDeletable(isDeletable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULES_DELETABLE] = isDeletable
        }
    }

    suspend fun setEventGroupVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_GROUPS_VISIBILITY] = visible
        }
    }

    suspend fun setEventRoomsVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_ROOMS_VISIBILITY] = visible
        }
    }

    suspend fun setEventLecturersVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_LECTURERS_VISIBILITY] = visible
        }
    }

    suspend fun setEventTagVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_TAG_VISIBILITY] = visible
        }
    }

    suspend fun setEventCommentVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_COMMENT_VISIBILITY] = visible
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

    val scheduleViewFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SCHEDULE_VIEW] ?: true
    }

    val schedulesDeletableFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SCHEDULES_DELETABLE] ?: true
    }

    val showCountClassesFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHOW_COUNT_CLASSES] ?: true
    }

    val groupsVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_GROUPS_VISIBILITY] ?: true
    }

    val roomsVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_ROOMS_VISIBILITY] ?: true
    }
    val lecturersVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_LECTURERS_VISIBILITY] ?: true
    }
    val tagVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_TAG_VISIBILITY] ?: true
    }
    val commentVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_COMMENT_VISIBILITY] ?: true
    }
}