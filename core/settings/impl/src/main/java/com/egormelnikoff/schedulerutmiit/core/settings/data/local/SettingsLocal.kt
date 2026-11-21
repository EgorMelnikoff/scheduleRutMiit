package com.egormelnikoff.schedulerutmiit.core.settings.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.AppSettings
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ReviewSettings
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ThemePreferences
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")

internal class SettingsLocal @Inject constructor(
    private val context: Context,
    private val json: Json
) {
    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setUsedImageInReview(usedImage: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USED_IMAGE_IN_REVIEW] = usedImage
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

    suspend fun skipWelcomePage() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SKIP_WELCOME_PAGE] = true
        }
    }

    suspend fun setEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EVENT_EXTRA_POLICY] = eventExtraPolicy.name
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

    suspend fun setLatestRelease(latestRelease: LatestRelease) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LATEST_RELEASE] = json.encodeToString(latestRelease)
        }
    }

    fun observeAppSettings() = context.dataStore.data.map { preferences ->
        AppSettings(
            themePreferences = preferences.getThemePreferences(),
            eventView = preferences.getEventView(),
            schedulesDeletable = preferences.getSchedulesDeletable(),
            usedImageInReview = preferences[PreferencesKeys.USED_IMAGE_IN_REVIEW] ?: true,
            skipWelcomePage = preferences[PreferencesKeys.SKIP_WELCOME_PAGE] ?: false,
            scheduleView = ScheduleView.entries.find {
                it.name == preferences[PreferencesKeys.SCHEDULE_VIEW]
            } ?: ScheduleView.CALENDAR,
            eventsCountView = EventsCountView.entries.find {
                it.name == preferences[PreferencesKeys.COUNT_CLASSES_VIEW]
            } ?: EventsCountView.DETAILS,
            eventExtraPolicy = preferences.getEventExtraPolicy()
        )
    }

    fun observeEventExtraPolicy() = context.dataStore.data.map { preferences ->
        preferences.getEventExtraPolicy()
    }

    fun observeSchedulesDeletable() = context.dataStore.data.map { preferences ->
        preferences.getSchedulesDeletable()
    }

    fun observeReviewSettings() = context.dataStore.data.map { preferences ->
        ReviewSettings(
            theme = preferences.getTheme(),
            usedImageInReview = preferences[PreferencesKeys.USED_IMAGE_IN_REVIEW] ?: true,
        )
    }

    fun observeThemePreferences() = context.dataStore.data.map { preferences ->
        preferences.getThemePreferences()
    }

    fun observeLatestRelease() = context.dataStore.data.map { preferences ->
        preferences.getLatestRelease()
    }

    fun Preferences.getEventView() = EventView(
        groupsVisible = this[PreferencesKeys.EVENT_GROUPS_VISIBILITY] ?: true,
        roomsVisible = this[PreferencesKeys.EVENT_ROOMS_VISIBILITY] ?: true,
        lecturersVisible = this[PreferencesKeys.EVENT_LECTURERS_VISIBILITY] ?: true,
        tagVisible = this[PreferencesKeys.EVENT_TAG_VISIBILITY] ?: true,
        commentVisible = this[PreferencesKeys.EVENT_COMMENT_VISIBILITY] ?: true,
    )

    fun Preferences.getThemePreferences() = ThemePreferences(
        theme = this.getTheme(),
        usedAmoled = this[PreferencesKeys.USED_AMOLED] ?: false,
        decorColorIndex = this[PreferencesKeys.DECOR_COLOR] ?: 0
    )

    fun Preferences.getTheme() = Theme.entries.find {
        it.name == this[PreferencesKeys.THEME]?.uppercase()
    } ?: Theme.SYSTEM


    fun Preferences.getLatestRelease(): LatestRelease? {
        return this[PreferencesKeys.LATEST_RELEASE]?.let {
            json.decodeFromString<LatestRelease>(it)
        }
    }

    fun Preferences.getEventExtraPolicy() = EventExtraPolicy.entries.find {
        it.name == this[PreferencesKeys.EVENT_EXTRA_POLICY]
    } ?: EventExtraPolicy.DEFAULT

    fun Preferences.getSchedulesDeletable() = this[PreferencesKeys.SCHEDULES_DELETABLE] ?: true
}