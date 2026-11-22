package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeInstantSerializer
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeOffsetSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class ScheduleEventDtoV2(
    @SerialName("idGroup")
    val idGroup: Long? = null,
    @SerialName("egName")
    val groupName: String? = null,
    @SerialName("empId")
    val personId: Long? = null,
    @SerialName("empFio")
    val personFio: String? = null,


    @SerialName("idSchedule")
    val eventId: Long,
    @SerialName("timetableTypeInt")
    val timetableType: Int,
    @SerialName("timetableTypeDisplay")
    val timetableTypeName: String,
    @SerialName("eduWeekNumber")
    val periodNumber: Int,
    @SerialName("isCurrentEduWeekInt")
    val isCurrentWeek: Int,
    @SerialName("textTitle")
    val typeName: String,
    @SerialName("text")
    val name: String,
    @Serializable(with = LocalDateTimeOffsetSerializer::class)
    @SerialName("eventStartDateTime")
    val startDateTime: LocalDateTime,
    @Serializable(with = LocalDateTimeOffsetSerializer::class)
    @SerialName("eventEndDateTime")
    val endDateTime: LocalDateTime,

    @SerialName("roomsXml")
    val rooms: String?,
    @SerialName("edGroupsXml")
    val groups: String?,
    @SerialName("lecturersXml")
    val lecturers: String?,

    @Serializable(with = LocalDateTimeInstantSerializer::class)
    @SerialName("validFrom")
    val validFrom: LocalDateTime,

    @Serializable(with = LocalDateTimeInstantSerializer::class)
    @SerialName("validTo")
    val validTo: LocalDateTime,

    @SerialName("idkPeriodicity")
    val isPeriodic: Int,

    @SerialName("slotName")
    val timeSlotName: String? = null
)