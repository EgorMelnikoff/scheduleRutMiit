package com.egormelnikoff.schedulerutmiit.core.settings.api.repos

import com.egormelnikoff.schedulerutmiit.core.settings.api.model.AppSettings
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ReviewSettings
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ThemePreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepos {
    suspend fun setTheme(theme: Theme)
    suspend fun setUsedImageInReview(usedImage: Boolean)
    suspend fun setUsedAmoled(usedAmoled: Boolean)
    suspend fun setDecorColor(color: Int)
    suspend fun setScheduleView(scheduleView: ScheduleView)
    suspend fun setSchedulesDeletable(isDeletable: Boolean)
    suspend fun skipWelcomePage()
    suspend fun setEventExtraPolicy(eventExtraPolicy: EventExtraPolicy)
    suspend fun setEventGroupVisibility(visible: Boolean)
    suspend fun setEventRoomsVisibility(visible: Boolean)
    suspend fun setEventLecturersVisibility(visible: Boolean)
    suspend fun setEventTagVisibility(visible: Boolean)
    suspend fun setEventCommentVisibility(visible: Boolean)
    suspend fun setEventCountView(eventsCountView: EventsCountView)
    suspend fun onSkipWelcomePage()

    fun observeAppSettings(): Flow<AppSettings>
    fun observeReviewSettings(): Flow<ReviewSettings>
    fun observeThemePreferences(): Flow<ThemePreferences>
    suspend fun updateLatestRelease(): LatestRelease
    suspend fun getLatestRelease() : LatestRelease?
    suspend fun getEventExtraPolicy() : EventExtraPolicy
    suspend fun getSchedulesDeletable(): Boolean

    suspend fun getCurrentVersion() : Int
}