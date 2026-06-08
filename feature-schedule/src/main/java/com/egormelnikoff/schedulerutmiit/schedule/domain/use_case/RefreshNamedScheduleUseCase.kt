package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.result.ScheduleUseCaseResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos,
    private val preferencesDataSource: PreferencesDataSource,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase
) {
    companion object {
        private val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(5)
    }

    suspend operator fun invoke(
        namedScheduleId: Long? = null,
        updating: Boolean = false
    ): ScheduleUseCaseResult {
        val namedSchedule = namedScheduleId?.let {
            namedScheduleRepos.getById(namedScheduleId).namedSchedule
        }
            ?: namedScheduleRepos.getDefault()
            ?: namedScheduleRepos.getAll().firstOrNull()

        if (namedSchedule != null && updating) {
            update(
                namedSchedule = namedSchedule,
                deletableOldSchedules = preferencesDataSource.schedulesDeletableFlow.first()
            )
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = namedScheduleRepos.getAll(),
            namedScheduleWithSchedules = namedSchedule?.let {
                namedScheduleRepos.getById(
                    namedScheduleId = it.id
                )
            }
        )
    }


    suspend fun update(
        namedSchedule: NamedSchedule,
        deletableOldSchedules: Boolean,
        onStartUpdate: (() -> Unit)? = null
    ): Result<String> {
        if (shouldUpdateNamedSchedule(namedSchedule)) {
            onStartUpdate?.invoke()
            return performNamedScheduleUpdate(namedSchedule, deletableOldSchedules)
        }
        return Result.Success("No schedule update required")
    }

    private fun shouldUpdateNamedSchedule(namedSchedule: NamedSchedule): Boolean {
        val timeSinceLastUpdate = System.currentTimeMillis() - namedSchedule.lastTimeUpdate
        val isNotCustomSchedule = namedSchedule.type != NamedScheduleType.MY
        return timeSinceLastUpdate > SCHEDULE_UPDATE_THRESHOLD_MS && isNotCustomSchedule
    }

    private suspend fun performNamedScheduleUpdate(
        namedSchedule: NamedSchedule,
        deletableOldSchedules: Boolean
    ): Result<String> {
        val result = fetchNamedScheduleUseCase(
            namedScheduleId = namedSchedule.id,
            fetchForce = true,
            name = namedSchedule.shortName,
            apiId = namedSchedule.apiId?.toInt() ?: 0,
            namedScheduleType = namedSchedule.type
        )

        return when (val updatedNamedSchedule = result.namedScheduleWithSchedules) {
            is Result.Error -> updatedNamedSchedule

            is Result.Success -> {
                val oldNamedSchedule = namedScheduleRepos.getById(namedSchedule.id)

                mergeAndUpdateSchedules(
                    oldNamedScheduleWithSchedules = oldNamedSchedule,
                    newNamedScheduleWithSchedules = updatedNamedSchedule.data,
                )

                if (deletableOldSchedules) {
                    deleteOldSchedules(
                        newNamedScheduleWithSchedules = updatedNamedSchedule.data,
                        oldNamedScheduleWithSchedules = oldNamedSchedule
                    )
                }

                namedScheduleRepos.updateLastTimeUpdate(
                    namedScheduleId = oldNamedSchedule.namedSchedule.id
                )

                Result.Success("Success update")
            }
        }
    }

    private suspend fun mergeAndUpdateSchedules(
        oldNamedScheduleWithSchedules: NamedScheduleWithSchedules,
        newNamedScheduleWithSchedules: NamedScheduleWithSchedules
    ) {
        val oldSchedulesMap = oldNamedScheduleWithSchedules.scheduleWithEvents
            .filter { schedule ->
                val isEmpty = schedule.events.isEmpty()
                if (isEmpty) scheduleRepos.deleteById(schedule.schedule.id)
                !isEmpty
            }
            .associateBy { it.schedule.getKey() }


        newNamedScheduleWithSchedules.scheduleWithEvents.forEach { updatedSchedule ->
            val oldSchedule = oldSchedulesMap[updatedSchedule.schedule.getKey()]

            if (oldSchedule != null) {
                val updatedEvents = mutableListOf<Event>()
                val customEvents = oldSchedule.events.filter { it.isCustomEvent }
                val defaultEvents = updatedSchedule.events.map { event ->
                    val oldEvent = oldSchedule.events.find { it.customEquals(event) }
                    event.copy(
                        id = oldEvent?.id ?: 0L,
                        isHidden = oldEvent?.isHidden ?: false
                    )
                }
                updatedEvents.addAll(defaultEvents)
                updatedEvents.addAll(customEvents)

                scheduleRepos.updateEvents(
                    scheduleId = oldSchedule.schedule.id,
                    scheduleWithEvents = ScheduleWithEvents(
                        schedule = updatedSchedule.schedule,
                        events = updatedEvents,
                        eventsExtraData = oldSchedule.eventsExtraData
                    )
                )
            } else {
                scheduleRepos.save(
                    oldNamedScheduleWithSchedules.namedSchedule.id,
                    updatedSchedule
                )
            }
        }
    }

    private suspend fun deleteOldSchedules(
        newNamedScheduleWithSchedules: NamedScheduleWithSchedules,
        oldNamedScheduleWithSchedules: NamedScheduleWithSchedules
    ) {
        oldNamedScheduleWithSchedules.scheduleWithEvents.forEach { oldSchedule ->
            val stillExists = newNamedScheduleWithSchedules.scheduleWithEvents.any {
                it.schedule.getKey() == oldSchedule.schedule.getKey()
            }
            val isOutdated = LocalDate.now() > oldSchedule.schedule.endDate
            if (!stillExists && isOutdated) {
                scheduleRepos.deleteById(
                    scheduleId = oldSchedule.schedule.id
                )
            }
        }
    }
}