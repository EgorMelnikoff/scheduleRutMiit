package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.repos.PreferencesRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.ScheduleDataSource
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RefreshNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val namedScheduleDataSource: NamedScheduleDataSource,
    private val scheduleDataSource: ScheduleDataSource,
    private val preferencesRepos: PreferencesRepos,
) {
    companion object {
        private val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.MINUTES.toMillis(5)
    }

    suspend operator fun invoke(
        namedScheduleId: Long? = null,
        updating: Boolean = false
    ) {
        val namedSchedule = namedScheduleId?.let {
            namedScheduleDataSource.getById(namedScheduleId).namedSchedule
        }
            ?: namedScheduleDataSource.getDefault()
            ?: namedScheduleRepos.getAll().firstOrNull()

        if (namedSchedule != null && updating) {
            update(
                namedSchedule = namedSchedule,
                deletableOldSchedules = preferencesRepos.schedulesDeletableFlow.first()
            )
        }
    }

    suspend fun update(
        namedSchedule: NamedSchedule,
        deletableOldSchedules: Boolean,
        onStartUpdate: (() -> Unit)? = null
    ): Boolean {
        if (shouldUpdateNamedSchedule(namedSchedule)) {
            onStartUpdate?.invoke()
            return performNamedScheduleUpdate(namedSchedule, deletableOldSchedules)
        }
        return false
    }

    private fun shouldUpdateNamedSchedule(namedSchedule: NamedSchedule): Boolean {
        val timeSinceLastUpdate = System.currentTimeMillis() - namedSchedule.lastTimeUpdate
        val isNotCustomSchedule = namedSchedule.type != NamedScheduleType.MY
        return timeSinceLastUpdate > SCHEDULE_UPDATE_THRESHOLD_MS && isNotCustomSchedule
    }

    private suspend fun performNamedScheduleUpdate(
        namedSchedule: NamedSchedule,
        deletableOldSchedules: Boolean
    ): Boolean {
        val updatedNamedSchedule = namedScheduleRepos.get(
            namedSchedule.id,
            namedSchedule.shortName,
            namedSchedule.apiId?.toInt() ?: 0,
            namedSchedule.type
        )

        val oldNamedSchedule = namedScheduleDataSource.getById(namedSchedule.id)

        mergeAndUpdateSchedules(
            oldNamedScheduleWithSchedules = oldNamedSchedule,
            newNamedScheduleWithSchedules = updatedNamedSchedule,
        )

        if (deletableOldSchedules) {
            deleteOldSchedules(
                newNamedScheduleWithSchedules = updatedNamedSchedule,
                oldNamedScheduleWithSchedules = oldNamedSchedule
            )
        }

        namedScheduleDataSource.updateLastTimeUpdate(
            namedScheduleId = oldNamedSchedule.namedSchedule.id
        )

        return true
    }

    private suspend fun mergeAndUpdateSchedules(
        oldNamedScheduleWithSchedules: NamedScheduleWithSchedules,
        newNamedScheduleWithSchedules: NamedScheduleWithSchedules
    ) {
        val oldSchedulesMap = oldNamedScheduleWithSchedules.schedulesWithEvents
            .filter { schedule ->
                val isEmpty = schedule.events.isEmpty()
                if (isEmpty) scheduleDataSource.deleteById(schedule.schedule.id)
                !isEmpty
            }
            .associateBy { it.schedule.getKey() }


        newNamedScheduleWithSchedules.schedulesWithEvents.forEach { updatedSchedule ->
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

                scheduleDataSource.updateEvents(
                    scheduleId = oldSchedule.schedule.id,
                    scheduleWithEvents = ScheduleWithEvents(
                        schedule = updatedSchedule.schedule,
                        events = updatedEvents,
                        eventsExtraData = oldSchedule.eventsExtraData
                    )
                )
            } else {
                scheduleDataSource.save(
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
        oldNamedScheduleWithSchedules.schedulesWithEvents.forEach { oldSchedule ->
            val stillExists = newNamedScheduleWithSchedules.schedulesWithEvents.any {
                it.schedule.getKey() == oldSchedule.schedule.getKey()
            }
            val isOutdated = LocalDate.now() > oldSchedule.schedule.endDate
            if (!stillExists && isOutdated) {
                scheduleDataSource.deleteById(
                    scheduleId = oldSchedule.schedule.id
                )
            }
        }
    }
}