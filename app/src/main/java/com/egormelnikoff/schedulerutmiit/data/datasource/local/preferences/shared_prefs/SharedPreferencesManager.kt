package com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.shared_prefs

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    context: Context
) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getBooleanPreference(
        name: String,
        initialValue: Boolean
    ): Boolean {
        return prefs.getBoolean(name, initialValue)
    }

    fun editBooleanPreference(
        name: String,
        value: Boolean
    ) {
        prefs.edit { putBoolean(name, value) }
    }
}