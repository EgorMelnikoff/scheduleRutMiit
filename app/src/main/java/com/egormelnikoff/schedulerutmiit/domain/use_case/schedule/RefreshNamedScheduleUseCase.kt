package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.Schedule
import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.remote.network.result.Result
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRepos: ScheduleRepos,
    private val preferencesDataStore: PreferencesDataStore,
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
            namedScheduleRepos.getById(namedScheduleId).namedScheduleEntity
        }
            ?: namedScheduleRepos.getDefault()
            ?: namedScheduleRepos.getAllEntities().firstOrNull()

        if (namedSchedule != null && updating) {
            update(
                namedScheduleEntity = namedSchedule,
                deletableOldSchedules = preferencesDataStore.schedulesDeletableFlow.first()
            )
        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = namedScheduleRepos.getAllEntities(),
            namedSchedule = namedSchedule?.let {
                namedScheduleRepos.getById(
                    namedScheduleId = it.id
                )
            }
        )
    }


    suspend fun update(
        namedScheduleEntity: NamedScheduleEntity,
        deletableOldSchedules: Boolean,
        onStartUpdate: (() -> Unit)? = null
    ): Result<String> {
        if (shouldUpdateNamedSchedule(namedScheduleEntity)) {
            onStartUpdate?.invoke()
            return performNamedScheduleUpdate(namedScheduleEntity, deletableOldSchedules)
        }
        return Result.Success("No schedule update required")
    }

    private fun shouldUpdateNamedSchedule(namedScheduleEntity: NamedScheduleEntity): Boolean {
        val timeSinceLastUpdate = System.currentTimeMillis() - namedScheduleEntity.lastTimeUpdate
        val isNotCustomSchedule = namedScheduleEntity.type != NamedScheduleType.MY
        return timeSinceLastUpdate > SCHEDULE_UPDATE_THRESHOLD_MS && isNotCustomSchedule
    }

    private suspend fun performNamedScheduleUpdate(
        namedScheduleEntity: NamedScheduleEntity,
        deletableOldSchedules: Boolean
    ): Result<String> {
        val result = fetchNamedScheduleUseCase(
            namedScheduleId = namedScheduleEntity.id,
            fetchForce = true,
            name = namedScheduleEntity.shortName,
            apiId = namedScheduleEntity.apiId?.toInt() ?: 0,
            namedScheduleType = namedScheduleEntity.type
        )

        return when (val updatedNamedSchedule = result.namedSchedule) {
            is Result.Error -> updatedNamedSchedule

            is Result.Success -> {
                val oldNamedSchedule = namedScheduleRepos.getById(namedScheduleEntity.id)

                mergeAndUpdateSchedules(
                    oldNamedSchedule = oldNamedSchedule,
                    newNamedSchedule = updatedNamedSchedule.data,
                    deletableOldSchedules = deletableOldSchedules
                )

                namedScheduleRepos.updateLastTimeUpdate(
                    namedScheduleId = oldNamedSchedule.namedScheduleEntity.id
                )

                Result.Success("Success update")
            }
        }
    }

    private suspend fun mergeAndUpdateSchedules(
        oldNamedSchedule: NamedSchedule,
        newNamedSchedule: NamedSchedule,
        deletableOldSchedules: Boolean
    ) {
        val oldSchedulesMap = oldNamedSchedule.schedules
            .filter { schedule ->
                val isEmpty = schedule.events.isEmpty()
                if (isEmpty) scheduleRepos.deleteById(schedule.scheduleEntity.id)
                !isEmpty
            }
            .associateBy { it.scheduleEntity.getKey() }

        newNamedSchedule.schedules.forEach { updatedSchedule ->
            val oldSchedule = oldSchedulesMap[updatedSchedule.scheduleEntity.getKey()]

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
                    scheduleId = oldSchedule.scheduleEntity.id,
                    schedule = Schedule(
                        scheduleEntity = updatedSchedule.scheduleEntity,
                        events = updatedEvents,
                        eventsExtraData = oldSchedule.eventsExtraData
                    )
                )
            } else {
                scheduleRepos.save(
                    oldNamedSchedule.namedScheduleEntity.id,
                    updatedSchedule
                )
            }
        }

        if (deletableOldSchedules) {
            deleteOldSchedules(
                newNamedSchedule,
                oldNamedSchedule
            )
        }
    }

    private suspend fun deleteOldSchedules(
        newNamedSchedule: NamedSchedule,
        oldNamedSchedule: NamedSchedule
    ) {
        oldNamedSchedule.schedules.forEach { oldSchedule ->
            val stillExists = newNamedSchedule.schedules.any {
                it.scheduleEntity.getKey() == oldSchedule.scheduleEntity.getKey()
            }
            val isOutdated = LocalDate.now() > oldSchedule.scheduleEntity.endDate
            if (!stillExists && isOutdated) {
                scheduleRepos.deleteById(
                    scheduleId = oldSchedule.scheduleEntity.id
                )
            }
        }
    }
}