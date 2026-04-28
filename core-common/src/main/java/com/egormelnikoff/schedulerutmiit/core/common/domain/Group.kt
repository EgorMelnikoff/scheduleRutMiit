package com.egormelnikoff.schedulerutmiit.core.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: Int = -1,
    val name: String
)