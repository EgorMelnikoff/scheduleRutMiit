package com.egormelnikoff.schedulerutmiit.search.data.remote.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PeopleDtoV2(
    @SerialName("size")
    val size: Int,
    @SerialName("simplePersons")
    val simplePersons: List<PersonDtoV2>
)