package com.egormelnikoff.schedulerutmiit.core.common.domain.model

data class EventView(
    val groupsVisible: Boolean,
    val roomsVisible: Boolean,
    val lecturersVisible: Boolean,
    val tagVisible: Boolean,
    val commentVisible: Boolean
)