package com.egormelnikoff.schedulerutmiit.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.ListenableWorker
import com.egormelnikoff.schedulerutmiit.app.logger.Logger
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.google.gson.Gson
import javax.inject.Inject

interface WidgetDataUpdater {
    suspend fun updateAll(): ListenableWorker.Result
}

class WidgetDataUpdaterImpl @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val context: Context,
    private val gson: Gson,
    private val logger: Logger
) : WidgetDataUpdater {
    override suspend fun updateAll(): ListenableWorker.Result {
        val namedScheduleEntity = scheduleRepos.getDefaultNamedScheduleEntity()
            ?: return ListenableWorker.Result.failure()
        logger.i("WidgetDataUpdater", "Default schedule:\n$namedScheduleEntity")
        val namedSchedule = scheduleRepos.getSavedNamedScheduleById(namedScheduleEntity.id)!!
        val widgetData = WidgetData.getWidgetData(namedSchedule)
        if (widgetData != null) {
            logger.i("WidgetDataUpdater", "Start widget update")
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
            prefs[EventsWidget.widgetDataKey] = gson.toJson(widgetData)
        }
        EventsWidget().update(context, glanceId)
    }
}