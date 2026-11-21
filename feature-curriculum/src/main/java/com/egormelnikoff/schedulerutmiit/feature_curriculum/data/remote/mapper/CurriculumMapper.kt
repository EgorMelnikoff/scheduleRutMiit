package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.model.SubjectDto
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model.Subject

internal fun MutableMap<String, MutableSet<String>>.toSubjects(): List<SubjectDto> {
    val result = mutableListOf<SubjectDto>()
    this.forEach { (subject, teachers) ->
        result.add(
            SubjectDto(
                title = subject,
                teachers = teachers
            )
        )
    }
    return result
}

internal fun List<SubjectDto>.toDomain(): List<Subject> {
    val map = mutableMapOf<String, MutableSet<String>>()

    forEach { subject ->
        subject.title.split(";").map {
            it.trim()
        }.forEach { splited ->
            map.getOrPut(splited) {
                mutableSetOf()
            }.addAll(subject.teachers)
        }
    }

    return map.map { (title, teachers) ->
        Subject(
            title = title,
            teachers = teachers
        )
    }
}