package com.egormelnikoff.schedulerutmiit.latest_release.domain.repos

import com.egormelnikoff.schedulerutmiit.latest_release.domain.model.LatestRelease

interface LatestReleaseRepos {
    suspend fun updateLatestRelease(): LatestRelease
    suspend fun getLatestRelease(): LatestRelease?
}