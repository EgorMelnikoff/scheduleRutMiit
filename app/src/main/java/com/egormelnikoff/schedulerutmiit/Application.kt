package com.egormelnikoff.schedulerutmiit

import android.app.Application

class ScheduleApplication : Application() {
    lateinit var container: AppContainerInterface

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}