package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.EventDao
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.event_extra.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateEventExtraDataUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventExtraRepos: EventExtraRepos,
    private val preferencesDataStore: PreferencesDataStore,
    private val eventDao: EventDao
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        scheduleId: Long,
        event: Event,
        tag: Int,
        comment: String
    ): ScheduleUseCaseResult {
        if (comment == "" && tag == 0) {
            synchronizeEventExtraAction(event) {
                eventExtraRepos.deleteByEvent(event)
            }
            return ScheduleUseCaseResult(
                savedNamedScheduleEntities = null,
                namedSchedule = namedScheduleRepos.getById(
                    namedScheduleId
                )
            )
        }

        val eventExtraData = eventExtraRepos.getByEventId(event.id)

        if (eventExtraData != null) {
            synchronizeEventExtraAction(event) {
                eventExtraRepos.updateComment(event, comment)
            }
            synchronizeEventExtraAction(event) {
                eventExtraRepos.updateTag(event, tag)
            }
        } else {
            synchronizeEventExtraAction(event) {
                eventExtraRepos.save(event, tag, comment)
            }

        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(namedScheduleId)
        )
    }

    suspend fun synchronizeEventExtraAction(
        event: Event,
        action: suspend (Event) -> Unit
    ) {
        if (preferencesDataStore.syncTagCommentsFlow.first()) {
            eventDao.getByNameAndType(
                event.name,
                event.typeName,
                event.scheduleId
            ).forEach { event ->
                action(event)
            }
        } else {
            action(event)
        }
    }
}