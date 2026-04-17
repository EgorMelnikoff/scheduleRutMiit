package com.egormelnikoff.schedulerutmiit.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.ListenableWorker
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetDataUpdater @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val context: Context,
    private val json: Json
) {
    suspend fun updateAll(): ListenableWorker.Result {
        namedScheduleRepos.getDefault()?.let { namedScheduleEntity ->
            val widgetData = WidgetData(
                namedSchedule = namedScheduleRepos.getById(namedScheduleEntity.id)
            )
            widgetData?.let {
                val widgetDataString = json.encodeToString(widgetData)
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