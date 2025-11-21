package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Lecturer
import com.egormelnikoff.schedulerutmiit.app.model.Room

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
        id = null,
        url = null,
        name = "",
        hint = ""
    )
    val defaultLecturer = Lecturer(
        id = null,
        shortFio = "",
        fullFio = "",
        url = null,
        description = null,
        hint = null
    )
    val defaultGroup = Group(
        id = null,
        url = null,
        name = ""
    )
}