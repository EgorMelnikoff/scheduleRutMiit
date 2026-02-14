package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.RefreshNamedScheduleResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val preferencesDataStore: PreferencesDataStore,
    private val fetchNamedScheduleUseCase: FetchNamedScheduleUseCase
) {
    companion object {
        private val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.HOURS.toMillis(10)
    }

    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long? = null,
        updating: Boolean = false
    ): RefreshNamedScheduleResult {
        val savedNamedSchedules = scheduleRepos.getSavedNamedSchedules()
        val namedScheduleToUpdate = savedNamedSchedules.find { namedScheduleEntity ->
            primaryKeyNamedSchedule?.let {
                namedScheduleEntity.id == it
            } ?: namedScheduleEntity.isDefault
        } ?: savedNamedSchedules.firstOrNull()

        if (namedScheduleToUpdate != null && updating) {
            update(
                namedScheduleEntity = namedScheduleToUpdate,
                deletableOldSchedules = preferencesDataStore.schedulesDeletableFlow.first()
            )
        }

        return RefreshNamedScheduleResult(
            savedNamedSchedules = scheduleRepos.getSavedNamedSchedules(),
            namedScheduleFormatted = namedScheduleToUpdate?.let {
                scheduleRepos.getNamedScheduleById(
                    primaryKeyNamedSchedule = it.id
                )
            },
            isSaved = namedScheduleToUpdate != null
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
            primaryKeyNamedSchedule = namedScheduleEntity.id,
            fetchForce = true,
            name = namedScheduleEntity.shortName,
            apiId = namedScheduleEntity.apiId!!.toInt(),
            namedScheduleType = namedScheduleEntity.type
        )

        return when (val updatedNamedSchedule = result.namedScheduleFormatted) {
            is Result.Error -> {
                Result.Error(updatedNamedSchedule.typedError)
            }

            is Result.Success -> {
                val oldNamedSchedule = scheduleRepos.getNamedScheduleById(namedScheduleEntity.id)
                if (oldNamedSchedule != null) {
                    mergeAndUpdateSchedules(
                        oldNamedSchedule = oldNamedSchedule,
                        newNamedSchedule = updatedNamedSchedule.data,
                        deletableOldSchedules = deletableOldSchedules
                    )
                    Result.Success("Success update")
                } else {
                    Result.Error(
                        TypedError.UnexpectedError(
                            Exception("Cannot find schedule")
                        )
                    )
                }
            }
        }
    }


    private suspend fun mergeAndUpdateSchedules(
        oldNamedSchedule: NamedScheduleFormatted,
        newNamedSchedule: NamedScheduleFormatted,
        deletableOldSchedules: Boolean
    ) {
        val oldSchedulesMap = oldNamedSchedule.schedules.associateBy {
            it.scheduleEntity.timetableId
        }

        newNamedSchedule.schedules.forEach { updatedSchedule ->
            oldSchedulesMap[updatedSchedule.scheduleEntity.timetableId]?.let { oldSchedule ->
                val updatedScheduleEntity =
                    if (oldSchedule.scheduleEntity.recurrence?.currentNumber != updatedSchedule.scheduleEntity.recurrence?.currentNumber) {
                        oldSchedule.scheduleEntity.copy(
                            recurrence = updatedSchedule.scheduleEntity.recurrence
                        )
                    } else {
                        oldSchedule.scheduleEntity
                    }

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

                val updatedScheduleWithId = ScheduleFormatted(
                    scheduleEntity = updatedScheduleEntity,
                    events = updatedEvents,
                    eventsExtraData = oldSchedule.eventsExtraData
                )
                scheduleRepos.deleteScheduleById(oldSchedule.scheduleEntity.id)
                scheduleRepos.insertSchedule(
                    oldNamedSchedule.namedScheduleEntity.id,
                    updatedScheduleWithId
                )
            } ?: scheduleRepos.insertSchedule(
                oldNamedSchedule.namedScheduleEntity.id,
                updatedSchedule
            )
        }

        if (deletableOldSchedules) {
            deleteOlSchedules(
                newNamedSchedule,
                oldNamedSchedule
            )
        }
        scheduleRepos.updateLastTimeUpdate(
            primaryKeyNamedSchedule = oldNamedSchedule.namedScheduleEntity.id,
            lastTimeUpdate = System.currentTimeMillis()
        )
    }

    private suspend fun deleteOlSchedules(
        newNamedSchedule: NamedScheduleFormatted,
        oldNamedSchedule: NamedScheduleFormatted
    ) {
        oldNamedSchedule.schedules.forEach { oldSchedule ->
            val stillExists = newNamedSchedule.schedules.any {
                it.scheduleEntity.timetableId == oldSchedule.scheduleEntity.timetableId
            }
            val isOutdated = LocalDate.now() > oldSchedule.scheduleEntity.endDate
            if (!stillExists && isOutdated) {
                scheduleRepos.deleteScheduleById(
                    primaryKeySchedule = oldSchedule.scheduleEntity.id
                )
            }
        }
    }
}