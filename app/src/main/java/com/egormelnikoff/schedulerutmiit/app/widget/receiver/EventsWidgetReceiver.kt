package com.egormelnikoff.schedulerutmiit.app.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget
import com.egormelnikoff.schedulerutmiit.di.ProviderEntryPoint
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
        val logger = entryPoint.logger()
        logger.i("EventsWidgetReceiver", "Start widget updating")
        workScheduler.startPeriodicWidgetUpdating()
    }

    private fun cancelWidgetUpdatingWork(context: Context) {
        val entryPoint = EntryPoints.get(context.applicationContext, ProviderEntryPoint::class.java)
        val workScheduler = entryPoint.workScheduler()
        val logger = entryPoint.logger()
        logger.i("EventsWidgetReceiver", "Cancel widget updating")
        workScheduler.cancelPeriodicWidgetUpdating()
    }
}