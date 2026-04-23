package com.egormelnikoff.schedulerutmiit.core.common

import com.egormelnikoff.schedulerutmiit.core.common.dto.GroupDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.LecturerDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.RoomDto

object DefaultEventParams {
    val types = arrayOf(
        null,
        "Лекция",
        "Лабораторная работа",
        "Практическое занятие",
        "Консультация",
        "Зачёт",
        "Экзамен",
        "Комиссия",
        "Другое"
    )
    val defaultRoom = RoomDto(
        id = -1,
        name = "",
        hint = ""
    )
    val defaultLecturer = LecturerDto(
        id = -1,
        shortFio = "",
        fullFio = "",
        hint = ""
    )
    val defaultGroup = GroupDto(
        id = -1,
        name = ""
    )
}