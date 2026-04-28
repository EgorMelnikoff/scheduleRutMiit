package com.egormelnikoff.schedulerutmiit.schedule.data.widget.receivers

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.egormelnikoff.schedulerutmiit.schedule.data.widget.ui.EventsWidget

class EventsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EventsWidget()
}