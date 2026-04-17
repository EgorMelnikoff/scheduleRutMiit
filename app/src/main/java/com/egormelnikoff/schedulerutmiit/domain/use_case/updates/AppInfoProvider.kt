package com.egormelnikoff.schedulerutmiit.domain.use_case.updates

interface AppInfoProvider {
    fun getVersionName(): String
}