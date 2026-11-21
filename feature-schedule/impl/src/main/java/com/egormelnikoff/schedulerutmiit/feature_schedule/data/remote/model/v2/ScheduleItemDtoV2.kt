package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.v2

import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeInstantSerializer
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeOffsetSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ScheduleItemDtoV2(
    @SerialName("idGroup")
    val idGroup: Long? = null,
    @SerialName("egName")
    val egName: String? = null,
    @SerialName("empId")
    val empId: Long? = null,
    @SerialName("empFio")
    val empFio: String? = null,


    @SerialName("idSchedule")
    val idSchedule: Long,
    @SerialName("timetableTypeInt")
    val timetableTypeInt: Int,
    @SerialName("timetableTypeDisplay")
    val timetableTypeDisplay: String,
    @SerialName("eduWeekNumber")
    val eduWeekNumber: Int,
    @SerialName("isCurrentEduWeekInt")
    val isCurrentEduWeekInt: Int,
    @SerialName("textTitle")
    val textTitle: String,
    @SerialName("text")
    val text: String,
    @Serializable(with = LocalDateTimeOffsetSerializer::class)
    @SerialName("eventStartDateTime")
    val eventStartDateTime: LocalDateTime,
    @Serializable(with = LocalDateTimeOffsetSerializer::class)
    @SerialName("eventEndDateTime")
    val eventEndDateTime: LocalDateTime,

    @SerialName("roomsXml")
    val roomsXml: String?,
    @SerialName("edGroupsXml")
    val edGroupsXml: String?,
    @SerialName("lecturersXml")
    val lecturersXml: String?,

    @Serializable(with = LocalDateTimeInstantSerializer::class)
    @SerialName("validFrom")
    val validFrom: LocalDateTime,

    @Serializable(with = LocalDateTimeInstantSerializer::class)
    @SerialName("validTo")
    val validTo: LocalDateTime,

    @SerialName("idkPeriodicity")
    val idkPeriodicity: Int,

    @SerialName("slotName")
    val slotName: String? = null
)