package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.model

import androidx.compose.runtime.Immutable
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Group
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Room
import java.time.LocalDate
import java.time.LocalDateTime

@Immutable
data class EventVo(
    val id: Long,
    val scheduleId: Long,
    val isCustomEvent: Boolean,
    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<Lecturer>?,
    val rooms: List<Room>?,
    val groups: List<Group>?,
    val comment: String?,
    val tag: Int?,
    val dialogDate: LocalDate?
)
