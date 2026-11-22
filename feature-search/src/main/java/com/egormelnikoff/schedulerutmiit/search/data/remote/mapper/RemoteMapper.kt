package com.egormelnikoff.schedulerutmiit.search.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.GroupDto
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.PersonDto
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2.GroupDtoV2
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2.PersonDtoV2
import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person

internal fun PersonDto.toDomain() = Person(name, id, "", position)

internal fun GroupDto.toDomain() = Group(id, name)

internal fun GroupDtoV2.toDomain() = Group(idGroup, egName)

internal fun PersonDtoV2.toDomain() = Person(
    name = fio,
    id = idE,
    picUrl = photoUrl?.let { url ->
        Endpoints.picUrlWithHost(url)
    },
    position = postNames.joinToString("\n") { it.postName }
)
