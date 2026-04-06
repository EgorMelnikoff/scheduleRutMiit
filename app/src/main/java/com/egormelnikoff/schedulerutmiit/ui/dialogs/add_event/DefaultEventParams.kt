package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.LecturerDto
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.RoomDto

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