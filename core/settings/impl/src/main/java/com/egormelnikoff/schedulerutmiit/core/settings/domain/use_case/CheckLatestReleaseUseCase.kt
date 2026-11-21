package com.egormelnikoff.schedulerutmiit.core.settings.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.settings.data.local.mapper.parseVersionCode
import javax.inject.Inject

class CheckLatestReleaseUseCase @Inject constructor(
    private val settingsRepos: SettingsRepos,
) {
    suspend operator fun invoke(): Boolean {
        val latest = settingsRepos.getLatestRelease()

        return latest?.let {
            it.tag.parseVersionCode() > settingsRepos.getCurrentVersion()
        } ?: false
    }
}