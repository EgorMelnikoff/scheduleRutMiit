package com.egormelnikoff.schedulerutmiit.search.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.search.data.remote.model.GroupDto
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.PersonDto
import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person

internal fun PersonDto.toDomain() = Person(name, id, position)

internal fun GroupDto.toDomain() = Group(id, name)
