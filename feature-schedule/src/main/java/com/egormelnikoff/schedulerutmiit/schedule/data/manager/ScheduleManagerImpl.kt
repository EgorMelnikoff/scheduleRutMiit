package com.egormelnikoff.schedulerutmiit.schedule.data.manager

import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.manager.ScheduleManager
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.SaveNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer.ObserveDefaultNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer.ObserveNamedScheduleByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ScheduleManagerImpl @Inject constructor(
    private val observeDefaultNamedScheduleUseCase: ObserveDefaultNamedScheduleUseCase,
    private val observeNamedScheduleByIdUseCase: ObserveNamedScheduleByIdUseCase,
    private val saveNamedScheduleUseCase: SaveNamedScheduleUseCase
) : ScheduleManager {

    private val source = MutableStateFlow<ScheduleSource>(ScheduleSource.Default)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentNamedSchedule: Flow<NamedScheduleWithSchedules?> =
        source.flatMapLatest { source ->
            when (source) {
                is ScheduleSource.ById -> observeNamedScheduleByIdUseCase(source.id)

                is ScheduleSource.Default -> observeDefaultNamedScheduleUseCase()

                is ScheduleSource.Temporary -> flowOf(source.schedule)
            }
        }

    override val isTemp: Boolean
        get() = source.value is ScheduleSource.Temporary

    override suspend fun openTemp(namedScheduleWithSchedules: NamedScheduleWithSchedules) {
        source.value = ScheduleSource.Temporary(namedScheduleWithSchedules)
    }

    override suspend fun openSaved(id: Long) {
        source.value = ScheduleSource.ById(id)
    }

    override suspend fun openDefault() {
        source.value = ScheduleSource.Default
    }

    override suspend fun saveTemp() {
        currentNamedSchedule.first()?.let {
            val result = saveNamedScheduleUseCase(it)
            source.value = if (result.second) ScheduleSource.Default
            else ScheduleSource.ById(result.first)
        }
    }
}