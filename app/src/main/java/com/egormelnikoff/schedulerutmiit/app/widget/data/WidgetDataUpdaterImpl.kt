package com.egormelnikoff.schedulerutmiit.app.widget.data

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.ListenableWorker
import com.egormelnikoff.schedulerutmiit.app.widget.ui.EventsWidget
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findDefaultSchedule
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetDataUpdaterImpl @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val preferencesDataSource: PreferencesDataSource,
    private val context: Context,
    private val json: Json
) : WidgetDataUpdater {
    override suspend fun updateAll(): ListenableWorker.Result {
        namedScheduleRepos.getDefault()?.let { namedSchedule ->
            val glanceIds = GlanceAppWidgetManager(context)
                .getGlanceIds(EventsWidget::class.java)

            if (glanceIds.isNotEmpty()) {
                val widgetData = WidgetData(
                    namedSchedule = namedSchedule,
                    scheduleWithEvents = namedScheduleRepos.getById(namedSchedule.id).scheduleWithEvents.findDefaultSchedule(),
                    eventExtraPolicy = preferencesDataSource.eventExtraPolicyFlow.first()
                )
                widgetData?.let {
                    val widgetDataString = json.encodeToString(widgetData)

                    glanceIds.forEach { glanceId ->
                        updateWidgetState(glanceId, widgetDataString)
                    }
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