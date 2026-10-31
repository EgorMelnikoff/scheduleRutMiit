package com.egormelnikoff.schedulerutmiit.latest_release.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.egormelnikoff.schedulerutmiit.core.common.consts.PreferencesKeys
import com.egormelnikoff.schedulerutmiit.latest_release.domain.model.LatestRelease
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "LatestRelease")

class LatestReleaseLocal @Inject constructor(
    private val context: Context,
    private val json: Json
) {
    suspend fun setLatestRelease(latestRelease: LatestRelease) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LATEST_RELEASE] = json.encodeToString(latestRelease)
        }
    }

    suspend fun getLatestRelease(): LatestRelease? {
        val preferences = context.dataStore.data.first()

        return preferences[PreferencesKeys.LATEST_RELEASE]?.let {
            json.decodeFromString<LatestRelease>(it)
        }
    }
}