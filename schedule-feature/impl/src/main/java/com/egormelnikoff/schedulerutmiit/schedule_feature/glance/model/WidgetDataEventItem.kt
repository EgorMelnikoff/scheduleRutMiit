package com.egormelnikoff.schedulerutmiit.schedule_feature.glance.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetDataEventItem(
    val name: String,
    val type: String?,
    val room: String? = null,
    val comment: String? = null
)