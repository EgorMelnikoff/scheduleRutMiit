package com.egormelnikoff.schedulerutmiit.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val ONE_TIME = "one_time"
    const val LIVE = "live"
}

fun createChannels(context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java)

    val oneTimeChannel = NotificationChannel(
        NotificationChannels.ONE_TIME,
        "Уведомления о занятиях",
        NotificationManager.IMPORTANCE_HIGH
    )


    val liveChannel = NotificationChannel(
        NotificationChannels.LIVE,
        "Прогресс пар на день",
        NotificationManager.IMPORTANCE_DEFAULT
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        manager.createNotificationChannels(listOf(oneTimeChannel, liveChannel))
    } else {
        manager.createNotificationChannel(oneTimeChannel)
    }

}