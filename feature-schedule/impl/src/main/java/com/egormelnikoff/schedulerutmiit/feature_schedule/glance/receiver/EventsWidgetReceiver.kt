package com.egormelnikoff.schedulerutmiit.feature_schedule.glance.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.egormelnikoff.schedulerutmiit.feature_schedule.di.ProviderEntryPoint
import com.egormelnikoff.schedulerutmiit.feature_schedule.glance.ui.EventsWidget
import dagger.hilt.EntryPoints

class EventsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventsWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_ENABLED -> {
                startWidgetUpdatingWork(context)
            }

            AppWidgetManager.ACTION_APPWIDGET_DISABLED -> {
                cancelWidgetUpdatingWork(context)
            }
        }
    }

    private fun startWidgetUpdatingWork(context: Context) {
        val entryPoint = EntryPoints.get(context.applicationContext, ProviderEntryPoint::class.java)
        val workScheduler = entryPoint.workScheduler()
        Log.i("EventsWidgetReceiver", "Start widget updating")
        workScheduler.startPeriodicWidgetUpdating()
    }

    private fun cancelWidgetUpdatingWork(context: Context) {
        val entryPoint = EntryPoints.get(context.applicationContext, ProviderEntryPoint::class.java)
        val workScheduler = entryPoint.workScheduler()
        Log.i("EventsWidgetReceiver", "Cancel widget updating")
        workScheduler.cancelPeriodicWidgetUpdating()
    }
}