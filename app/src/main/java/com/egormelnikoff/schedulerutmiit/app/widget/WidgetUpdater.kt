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
        val namedScheduleEntity = scheduleRepos.getDefaultNamedScheduleEntity()
            ?: return ListenableWorker.Result.failure()
        val namedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleEntity.id)!!
        val widgetData = WidgetData.widgetData(namedSchedule)
        widgetData?.let {
            GlanceAppWidgetManager(context).getGlanceIds(EventsWidget::class.java)
                .forEach { glanceId ->
                    updateWidgetState(glanceId, widgetData)
                }
            return ListenableWorker.Result.success()
        }
        return ListenableWorker.Result.failure()
    }

    private suspend fun updateWidgetState(glanceId: GlanceId, widgetData: WidgetData) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val result = gson.toJson(widgetData)
            prefs[EventsWidget.widgetDataKey] = result
        }
        EventsWidget().update(context, glanceId)
    }
}