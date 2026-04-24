package com.egormelnikoff.schedulerutmiit.schedule.widget.receivers

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.egormelnikoff.schedulerutmiit.schedule.di.ProviderEntryPoint
import com.egormelnikoff.schedulerutmiit.schedule.widget.ui.EventsWidget
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Keep
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
        val scheduler = entryPoint.scheduler()
        val logger = entryPoint.logger()
        logger.i("EventsWidgetReceiver", "Start widget updating")
        CoroutineScope(Dispatchers.Default).launch {
            scheduler.startPeriodicWidgetUpdating()
        }
    }

    private fun cancelWidgetUpdatingWork(context: Context) {
        val entryPoint = EntryPoints.get(context.applicationContext, ProviderEntryPoint::class.java)
        val scheduler = entryPoint.scheduler()
        val logger = entryPoint.logger()
        logger.i("EventsWidgetReceiver", "Cancel widget updating")
        CoroutineScope(Dispatchers.Default).launch {
            scheduler.cancelPeriodicWidgetUpdating()
        }
    }
}