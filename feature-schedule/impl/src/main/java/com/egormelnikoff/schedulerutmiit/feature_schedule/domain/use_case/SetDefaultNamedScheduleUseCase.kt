package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.WidgetDataUpdater
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