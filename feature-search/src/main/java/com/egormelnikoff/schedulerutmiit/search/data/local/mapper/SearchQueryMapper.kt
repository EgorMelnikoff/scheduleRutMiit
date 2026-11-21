package com.egormelnikoff.schedulerutmiit.search.data.local.mapper

import com.egormelnikoff.schedulerutmiit.search.data.local.entity.SearchQueryEntity
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery

internal fun SearchQueryEntity.toDomain() = SearchQuery(id, name, apiId, namedScheduleType)
internal fun SearchQuery.toEntity() = SearchQueryEntity(id, name, apiId, namedScheduleType)