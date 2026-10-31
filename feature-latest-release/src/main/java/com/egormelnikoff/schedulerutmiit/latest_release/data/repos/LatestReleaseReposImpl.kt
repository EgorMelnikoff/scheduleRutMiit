package com.egormelnikoff.schedulerutmiit.latest_release.data.repos

import com.egormelnikoff.schedulerutmiit.latest_release.data.local.LatestReleaseLocal
import com.egormelnikoff.schedulerutmiit.latest_release.data.remote.LatestReleaseRemote
import com.egormelnikoff.schedulerutmiit.latest_release.domain.model.LatestRelease
import com.egormelnikoff.schedulerutmiit.latest_release.domain.repos.LatestReleaseRepos
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LatestReleaseReposImpl @Inject constructor(
    private val latestReleaseRemote: LatestReleaseRemote,
    private val latestReleaseLocal: LatestReleaseLocal,
) : LatestReleaseRepos {
    override suspend fun updateLatestRelease(): LatestRelease {
        val latestRelease = latestReleaseRemote.fetchLatestRelease()
        latestReleaseLocal.setLatestRelease(latestRelease)
        return latestRelease
    }

    override suspend fun getLatestRelease(): LatestRelease? {
        val latestRelease = latestReleaseLocal.getLatestRelease()

        return if (latestRelease?.isOutdated(UPDATE_THRESHOLD_MS) == true) {
            updateLatestRelease()
        } else {
            latestRelease
        }
    }

    companion object {
        val UPDATE_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(5)
    }
}