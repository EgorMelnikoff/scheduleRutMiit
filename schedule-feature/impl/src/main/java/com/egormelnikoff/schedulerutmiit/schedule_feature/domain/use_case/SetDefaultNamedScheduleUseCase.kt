package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.repos.WidgetDataUpdater
import javax.inject.Inject

class SetDefaultNamedScheduleUseCase @Inject constructor(
    private val namedScheduleDataSource: NamedScheduleDataSource,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        namedScheduleId: Long
    ) {
        namedScheduleDataSource.setDefault(namedScheduleId)
        widgetDataUpdater.updateAll()
    }
}