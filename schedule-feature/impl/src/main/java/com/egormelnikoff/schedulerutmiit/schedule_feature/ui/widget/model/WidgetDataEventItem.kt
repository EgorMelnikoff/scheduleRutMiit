package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.widget.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetDataEventItem(
    val name: String,
    val type: String?,
    val room: String? = null,
    val comment: String? = null
)