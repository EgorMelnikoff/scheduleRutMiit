package com.egormelnikoff.schedulerutmiit.schedule.ui.utils

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room

val lecturerMock = Lecturer(
    fullFio = "Антонова Елена Вячеславовна",
    shortFio = "Антонова Е. В."
)

val groupMock = Group(
    id = 0,
    name = "УВП-211"
)
val roomMock = Room(
    name = "Аудитория 1333",
    hint = "ГУК-1, 1333"
)
