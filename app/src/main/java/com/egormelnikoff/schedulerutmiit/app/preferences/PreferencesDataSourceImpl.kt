package com.egormelnikoff.schedulerutmiit.app.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.common.enums.Theme
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

class PreferencesDataSourceImpl @Inject constructor(
    private val context: Context,
    private val json: Json
): PreferencesDataSource {
    override suspend fun setLatestRelease(latestRelease: LatestRelease) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LATEST_RELEASE] = json.encodeToString(latestRelease)
        }
    }

    override suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    override suspend fun setUsedAmoled(usedAmoled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USED_AMOLED] = usedAmoled
        }
    }

    override suspend fun setDecorColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DECOR_COLOR] = color
        }
    }

    override suspend fun setScheduleView(scheduleView: ScheduleView) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULE_VIEW] = scheduleView.name
        }
    }

    override suspend fun setSchedulesDeletable(isDeletable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULES_DELETABLE] = isDeletable
        }
    }

    override suspend fun skipWelcomePage() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SKIP_WELCOME_PAGE] = true
        }
    }

    override suspend fun setEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_EXTRA_POLICY] = eventExtraPolicy.name
        }
    }

    override suspend fun setEventGroupVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_GROUPS_VISIBILITY] = visible
        }
    }

    override suspend fun setEventRoomsVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_ROOMS_VISIBILITY] = visible
        }
    }

    override suspend fun setEventLecturersVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_LECTURERS_VISIBILITY] = visible
        }
    }

    override suspend fun setEventTagVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_TAG_VISIBILITY] = visible
        }
    }

    override suspend fun setEventCommentVisibility(visible: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_COMMENT_VISIBILITY] = visible
        }
    }


    override suspend fun setEventCountView(eventsCountView: EventsCountView) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNT_CLASSES_VIEW] = eventsCountView.name
        }
    }

    override val latestReleaseFlow: Flow<LatestRelease?> =
        context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.LATEST_RELEASE]?.let {
                json.decodeFromString<LatestRelease>(it)
            }
        }

    override val themeFlow: Flow<Theme> = context.dataStore.data.map { preferences ->
        val theme = preferences[PreferencesKeys.THEME]
        Theme.entries.find { it.name == theme?.uppercase() } ?: Theme.SYSTEM
    }

    override val usedAmoledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USED_AMOLED] ?: false
    }

    override val decorColorFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DECOR_COLOR] ?: 0
    }

    override val skipWelcomeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SKIP_WELCOME_PAGE] ?: false
    }

    override val scheduleViewFlow: Flow<ScheduleView> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.SCHEDULE_VIEW]
        ScheduleView.entries.find { it.name == name } ?: ScheduleView.CALENDAR
    }

    override val schedulesDeletableFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SCHEDULES_DELETABLE] ?: true
    }

    override val eventExtraPolicyFlow: Flow<EventExtraPolicy> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.EVENT_EXTRA_POLICY]
        EventExtraPolicy.entries.find { it.name == name } ?: EventExtraPolicy.DEFAULT
    }




    override val eventCountViewFlow: Flow<EventsCountView> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.COUNT_CLASSES_VIEW]
        EventsCountView.entries.find { it.name == name } ?: EventsCountView.DETAILS
    }

    override val groupsVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_GROUPS_VISIBILITY] ?: true
    }

    override val roomsVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_ROOMS_VISIBILITY] ?: true
    }
    override val lecturersVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_LECTURERS_VISIBILITY] ?: true
    }
    override val tagVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_TAG_VISIBILITY] ?: true
    }
    override val commentVisibilityFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EVENT_COMMENT_VISIBILITY] ?: true
    }
}