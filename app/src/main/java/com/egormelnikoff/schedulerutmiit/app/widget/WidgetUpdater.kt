package com.egormelnikoff.schedulerutmiit.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.ListenableWorker
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.google.gson.Gson
import javax.inject.Inject

class WidgetDataUpdater @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val context: Context,
    private val gson: Gson
) {
    suspend fun updateAll(): ListenableWorker.Result {
        scheduleRepos.getDefaultNamedScheduleEntity()?.let { namedScheduleEntity ->
            val widgetData = WidgetData(
                namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleEntity.id)!!
            )
            widgetData?.let {
                val widgetDataString = gson.toJson(widgetData)
                GlanceAppWidgetManager(context)
                    .getGlanceIds(EventsWidget::class.java)
                    .forEach { glanceId ->
                        updateWidgetState(glanceId, widgetDataString)
                    }
            }
        }
        return ListenableWorker.Result.success()
    }

    private suspend fun updateWidgetState(glanceId: GlanceId, widgetDataString: String) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[EventsWidget.widgetDataKey] = widgetDataString
        }
        EventsWidget().update(context, glanceId)
    }
}