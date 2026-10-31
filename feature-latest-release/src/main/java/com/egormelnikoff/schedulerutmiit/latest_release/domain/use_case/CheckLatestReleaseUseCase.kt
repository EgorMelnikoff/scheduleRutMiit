package com.egormelnikoff.schedulerutmiit.latest_release.domain.use_case

import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.AppInfoProvider
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseRepos
import javax.inject.Inject

class CheckLatestReleaseUseCase @Inject constructor(
    private val latestReleaseRepos: LatestReleaseRepos,
    private val appInfoProvider: AppInfoProvider
) {
    suspend operator fun invoke(): Boolean {
        val latest = latestReleaseRepos.getLatestRelease()

        return latest?.let {
            it.tag.parseVersionCode() > appInfoProvider.getVersionName().parseVersionCode()
        } ?: false
    }

    private fun String.parseVersionCode(): Int {
        val clean = this.removePrefix("v")
        val parts = clean.split(".")

        val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

        return major * 10000 + minor * 100 + patch
    }
}