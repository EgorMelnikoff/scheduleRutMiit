package com.egormelnikoff.schedulerutmiit.feature_schedule.glance.model

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.ListenableWorker
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.findDefault
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.feature_schedule.glance.ui.EventsWidget
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetDataUpdaterImpl @Inject constructor(
    private val namedScheduleDataSource: NamedScheduleDataSource,
    private val settingsRepos: SettingsRepos,
    private val context: Context,
    private val json: Json
) : WidgetDataUpdater {
    override suspend fun updateAll(): ListenableWorker.Result {
        namedScheduleDataSource.getDefault()?.let { namedSchedule ->
            val glanceIds = GlanceAppWidgetManager(context)
                .getGlanceIds(EventsWidget::class.java)

            if (glanceIds.isNotEmpty()) {
                val widgetData = WidgetData(
                    namedSchedule = namedSchedule,
                    scheduleWithEvents = namedScheduleDataSource.getById(namedSchedule.id).schedulesWithEvents.findDefault(),
                    eventExtraPolicy = settingsRepos.getEventExtraPolicy()
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