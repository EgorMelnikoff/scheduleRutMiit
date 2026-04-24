package com.egormelnikoff.schedulerutmiit.latest_release.domain.repos

interface AppInfoProvider {
    fun getVersionName(): String
}