package com.egormelnikoff.schedulerutmiit.data.repos.settings

import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.PreferencesDataStore
import javax.inject.Inject

interface SettingsRepos {
    fun sendLogsFile()
    suspend fun onSetShowCountClasses(showCountClasses: Boolean)
    suspend fun onSetTheme(theme: String)
    suspend fun onSetDecorColor(decorColor: Int)
    suspend fun onSetScheduleView(scheduleView: Boolean)

    suspend fun onSetEventView(visible: Boolean)
    suspend fun onSetEventGroupVisibility(visible: Boolean)
    suspend fun onSetEventRoomsVisibility(visible: Boolean)
    suspend fun onSetEventLecturersVisibility(visible: Boolean)
    suspend fun onSetEventTagVisibility(visible: Boolean)
    suspend fun onSetEventCommentVisibility(visible: Boolean)
}

class SettingsReposImpl @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val logger: Logger
) : SettingsRepos {

    override fun sendLogsFile() {
        logger.sendLogsFile()
    }

    override suspend fun onSetEventGroupVisibility(visible: Boolean) {
        preferencesDataStore.setEventGroupVisibility(visible)
    }

    override suspend fun onSetEventView(visible: Boolean) {
        preferencesDataStore.setEventGroupVisibility(visible)
        preferencesDataStore.setEventRoomsVisibility(visible)
        preferencesDataStore.setEventLecturersVisibility(visible)
        preferencesDataStore.setEventTagVisibility(visible)
        preferencesDataStore.setEventCommentVisibility(visible)
    }

    override suspend fun onSetEventRoomsVisibility(visible: Boolean) {
        preferencesDataStore.setEventRoomsVisibility(visible)
    }

    override suspend fun onSetEventLecturersVisibility(visible: Boolean) {
        preferencesDataStore.setEventLecturersVisibility(visible)
    }

    override suspend fun onSetEventTagVisibility(visible: Boolean) {
        preferencesDataStore.setEventTagVisibility(visible)
    }

    override suspend fun onSetEventCommentVisibility(visible: Boolean) {
        preferencesDataStore.setEventCommentVisibility(visible)
    }

    override suspend fun onSetShowCountClasses(
        showCountClasses: Boolean
    ) {
        preferencesDataStore.setShowCountClasses(showCountClasses)
    }

    override suspend fun onSetTheme(
        theme: String
    ) {
        preferencesDataStore.setTheme(theme)
    }

    override suspend fun onSetDecorColor(
        decorColor: Int
    ) {
        preferencesDataStore.setDecorColor(decorColor)
    }

    override suspend fun onSetScheduleView(
        scheduleView: Boolean
    ) {
        preferencesDataStore.setScheduleView(scheduleView)
    }
}