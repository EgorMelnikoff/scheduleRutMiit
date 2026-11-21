package com.egormelnikoff.schedulerutmiit.core.settings.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.core.settings.api.model.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.settings.data.remote.dto.LatestReleaseDto

internal fun LatestReleaseDto.toDomain() = LatestRelease(url, name, tag)

