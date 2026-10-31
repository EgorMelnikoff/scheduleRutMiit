package com.egormelnikoff.schedulerutmiit.latest_release.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.latest_release.data.remote.dto.LatestReleaseDto
import com.egormelnikoff.schedulerutmiit.latest_release.domain.model.LatestRelease

fun LatestReleaseDto.toDomain() = LatestRelease(url, name, tag)
