package com.egormelnikoff.schedulerutmiit.domain.updates

interface AppInfoProvider {
    fun getVersionName(): String
}