package com.egormelnikoff.schedulerutmiit.app.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.app.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.app.enums.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

class PreferencesDataStore @Inject constructor(
    private val context: Context
) {
    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setUsedAmoled(usedAmoled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USED_AMOLED] = usedAmoled
        }
    }

    suspend fun setDecorColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DECOR_COLOR] = color
        }
    }

    suspend fun setScheduleView(scheduleView: ScheduleView) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULE_VIEW] = scheduleView.name
        }
    }

    suspend fun setSchedulesDeletable(isDeletable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULES_DELETABLE] = isDeletable
        }
    }

    suspend fun setSyncTagsComments(isSynchronizable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SYNC_TAGS_AND_COMMENTS] = isSynchronizable
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


    suspend fun setEventCountView(eventsCountView: EventsCountView) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNT_CLASSES_VIEW] = eventsCountView.name
        }
    }

    val themeFlow: Flow<Theme> = context.dataStore.data.map { preferences ->
        val theme = preferences[PreferencesKeys.THEME]
        Theme.entries.find { it.name == theme?.uppercase() } ?: Theme.SYSTEM
    }

    val usedAmoledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USED_AMOLED] ?: false
    }

    val decorColorFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DECOR_COLOR] ?: 0
    }

    val scheduleViewFlow: Flow<ScheduleView> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.SCHEDULE_VIEW]
        ScheduleView.entries.find { it.name == name } ?: ScheduleView.CALENDAR
    }

    val schedulesDeletableFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SCHEDULES_DELETABLE] ?: true
    }

    val syncTagCommentsFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SYNC_TAGS_AND_COMMENTS] ?: false
    }

    val eventCountViewFlow: Flow<EventsCountView> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.COUNT_CLASSES_VIEW]
        EventsCountView.entries.find { it.name == name } ?: EventsCountView.DETAILS
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