package com.egormelnikoff.schedulerutmiit.core.network.dto

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room

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
    val defaultRoom = Room(
        id = -1,
        name = "",
        hint = ""
    )
    val defaultLecturer = Lecturer(
        id = -1,
        shortFio = "",
        fullFio = "",
        hint = ""
    )
    val defaultGroup = Group(
        id = -1,
        name = ""
    )
}