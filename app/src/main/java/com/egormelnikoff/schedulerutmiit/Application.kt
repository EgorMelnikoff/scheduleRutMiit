package com.egormelnikoff.schedulerutmiit

import android.app.Application

class ScheduleApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}