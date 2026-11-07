package com.egormelnikoff.schedulerutmiit.core.settings.data.repos

import com.egormelnikoff.schedulerutmiit.core.settings.data.local.AppInfoProvider
import com.egormelnikoff.schedulerutmiit.core.settings.data.local.SettingsLocal
import com.egormelnikoff.schedulerutmiit.core.settings.data.local.mapper.parseVersionCode
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.Theme
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.settings.data.remote.LatestReleaseRemote
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class SettingsReposImpl @Inject constructor(
    private val latestReleaseRemote: LatestReleaseRemote,
    private val settingsLocal: SettingsLocal,
    private val appInfoProvider: AppInfoProvider
) : SettingsRepos {
    override suspend fun setTheme(theme: Theme) {
        settingsLocal.setTheme(theme)
    }

    override suspend fun setUsedImageInReview(usedImage: Boolean) {
        settingsLocal.setUsedImageInReview(usedImage)
    }

    override suspend fun setUsedAmoled(usedAmoled: Boolean) {
        settingsLocal.setUsedAmoled(usedAmoled)
    }

    override suspend fun setDecorColor(color: Int) {
        settingsLocal.setDecorColor(color)
    }

    override suspend fun setScheduleView(scheduleView: ScheduleView) {
        settingsLocal.setScheduleView(scheduleView)
    }

    override suspend fun setSchedulesDeletable(isDeletable: Boolean) {
        settingsLocal.setSchedulesDeletable(isDeletable)
    }

    override suspend fun skipWelcomePage() {
        settingsLocal.skipWelcomePage()
    }

    override suspend fun setEventExtraPolicy(eventExtraPolicy: EventExtraPolicy) {
        settingsLocal.setEventExtraPolicy(eventExtraPolicy)
    }

    override suspend fun setEventGroupVisibility(visible: Boolean) {
        settingsLocal.setEventGroupVisibility(visible)
    }

    override suspend fun setEventRoomsVisibility(visible: Boolean) {
        settingsLocal.setEventRoomsVisibility(visible)
    }

    override suspend fun setEventLecturersVisibility(visible: Boolean) {
        settingsLocal.setEventLecturersVisibility(visible)
    }

    override suspend fun setEventTagVisibility(visible: Boolean) {
        settingsLocal.setEventTagVisibility(visible)
    }

    override suspend fun setEventCommentVisibility(visible: Boolean) {
        settingsLocal.setEventCommentVisibility(visible)
    }

    override suspend fun setEventCountView(eventsCountView: EventsCountView) {
        settingsLocal.setEventCountView(eventsCountView)
    }

    override suspend fun onSkipWelcomePage() {
        settingsLocal.skipWelcomePage()
    }

    override fun observeAppSettings() = settingsLocal.observeAppSettings()

    override fun observeReviewSettings() = settingsLocal.observeReviewSettings()

    override fun observeThemePreferences() = settingsLocal.observeThemePreferences()

    override suspend fun updateLatestRelease(): LatestRelease {
        val latestRelease = latestReleaseRemote.fetchLatestRelease()
        settingsLocal.setLatestRelease(latestRelease)
        return latestRelease
    }

    override suspend fun getLatestRelease(): LatestRelease? {
        val latestRelease = settingsLocal.observeLatestRelease().first()

        return if (latestRelease?.isOutdated(UPDATE_THRESHOLD_MS) == true) {
            updateLatestRelease()
        } else {
            latestRelease
        }
    }

    override suspend fun getEventExtraPolicy(): EventExtraPolicy {
       return settingsLocal.observeEventExtraPolicy().first()
    }

    override suspend fun getSchedulesDeletable(): Boolean {
        return settingsLocal.observeSchedulesDeletable().first()
    }

    override suspend fun getCurrentVersion(): Int {
        return appInfoProvider.getVersionName().parseVersionCode()
    }

    companion object {
        val UPDATE_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(5)
    }
}