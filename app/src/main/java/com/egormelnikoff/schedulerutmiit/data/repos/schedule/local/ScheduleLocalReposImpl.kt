package com.egormelnikoff.schedulerutmiit.data.repos.schedule.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.local.db.Dao
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ScheduleLocalReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val dao: Dao,
    private val preferencesDataStore: PreferencesDataStore
) : ScheduleLocalRepos {
    override suspend fun saveNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ) = db.withTransaction {
        val namedScheduleId = dao.insertNamedScheduleEntity(namedSchedule.namedScheduleEntity)

        namedSchedule.schedules.forEach { scheduleFormatted ->
            insertEventsWithExtra(
                scheduleFormatted = scheduleFormatted,
                scheduleId = dao.insertScheduleEntity(
                    scheduleFormatted.scheduleEntity.copy(
                        namedScheduleId = namedScheduleId
                    )
                )
            )
        }

        if (dao.getCount() == 1) {
            dao.setDefaultNamedSchedule(namedScheduleId)
        }

        return@withTransaction namedScheduleId
    }

    override suspend fun saveSchedule(
        primaryKeyNamedSchedule: Long,
        scheduleFormatted: ScheduleFormatted
    ) = db.withTransaction {
        insertEventsWithExtra(
            scheduleFormatted = scheduleFormatted,
            scheduleId = dao.insertScheduleEntity(
                scheduleFormatted.scheduleEntity.copy(
                    namedScheduleId = primaryKeyNamedSchedule
                )
            )
        )
    }

    private suspend fun insertEventsWithExtra(
        scheduleFormatted: ScheduleFormatted,
        scheduleId: Long
    ) {
        scheduleFormatted.eventsExtraData.forEach { eventExtraData ->
            dao.insertEventExtraData(
                eventExtraData.copy(
                    scheduleId = scheduleId
                )
            )
        }

        scheduleFormatted.events.forEach { event ->
            dao.insertEvent(
                event.copy(
                    scheduleId = scheduleId
                )
            )
        }
    }

    override suspend fun insertEvent(event: EventEntity) = dao.insertEvent(event)

    override suspend fun insertEventExtraData(
        event: EventEntity,
        tag: Int,
        comment: String
    ) {
        synchronizableEventExtraAction(
            event
        ) { event ->
            dao.insertEventExtraData(
                EventExtraData(
                    id = event.id,
                    scheduleId = event.scheduleId,
                    eventName = event.name,
                    eventStartDatetime = event.startDatetime,
                    comment = comment,
                    tag = tag
                )
            )
        }
    }

    override suspend fun updateComment(
        primaryKeySchedule: Long,
        event: EventEntity,
        comment: String
    ) {
        synchronizableEventExtraAction(
            event
        ) { event ->
            dao.updateCommentEvent(primaryKeySchedule, event.id, comment)
        }
    }

    override suspend fun updateTag(
        primaryKeySchedule: Long,
        event: EventEntity,
        tag: Int
    ) {
        synchronizableEventExtraAction(
            event
        ) { event ->
            dao.updateTagEvent(primaryKeySchedule, event.id, tag)
        }
    }

    private suspend fun synchronizableEventExtraAction(
        event: EventEntity,
        action: suspend (EventEntity) -> Unit
    ) {
        if (preferencesDataStore.syncTagCommentsFlow.first()) {
            dao.getEventsByNameAndType(
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

    override suspend fun replaceScheduleEvents(
        oldScheduleId: Long,
        newScheduleFormatted: ScheduleFormatted
    ) = db.withTransaction {
        dao.deleteEventsByScheduleId(oldScheduleId)
        dao.deleteEventsExtraByScheduleId(oldScheduleId)
        insertEventsWithExtra(newScheduleFormatted, oldScheduleId)
    }


    override suspend fun getSavedNamedSchedules() = dao.getAllNamedScheduleEntities()

    override suspend fun getNamedScheduleById(
        primaryKeyNamedSchedule: Long
    ) = dao.getNamedScheduleById(primaryKeyNamedSchedule)


    override suspend fun getNamedScheduleByApiId(
        apiId: Int
    ) = dao.getNamedScheduleByApiId(apiId)


    override suspend fun getDefaultNamedScheduleEntity(
    ) = dao.getDefaultNamedScheduleEntity()


    override suspend fun getEventExtraByEventId(
        primaryKeyEvent: Long
    ) = dao.getEventExtraByEventId(primaryKeyEvent)

    override suspend fun getCountEventsPerDate(
        date: String,
        scheduleId: Long
    ) = dao.getCountEventsPerDate(date, scheduleId)

    override suspend fun updatePrioritySavedNamedSchedules(
        primaryKeyNamedSchedule: Long
    ) = db.withTransaction {
        dao.setDefaultNamedSchedule(primaryKeyNamedSchedule)
        dao.setNonDefaultNamedSchedule(primaryKeyNamedSchedule)
    }


    override suspend fun updatePrioritySchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    ) = db.withTransaction {
        dao.setDefaultSchedule(primaryKeySchedule)
        dao.setNonDefaultSchedule(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            primaryKeySchedule = primaryKeySchedule
        )
    }


    override suspend fun updateNamedScheduleName(
        primaryKeyNamedSchedule: Long,
        type: NamedScheduleType,
        newName: String
    ) = dao.updateName(
        primaryKeyNamedSchedule = primaryKeyNamedSchedule,
        fullName = newName,
        shortName = newName
    )


    override suspend fun updateLastTimeUpdate(primaryKeyNamedSchedule: Long) =
        dao.updateLastTimeUpdate(primaryKeyNamedSchedule)


    override suspend fun updateEvent(event: EventEntity) = db.withTransaction {
        dao.deleteEventById(event.id)
        dao.insertEvent(event)
    }

    override suspend fun updateEventIsHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    ) = dao.updateEventHidden(eventPrimaryKey, isHidden)


    override suspend fun deleteNamedScheduleById(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) = db.withTransaction {
        dao.deleteNamedScheduleById(primaryKeyNamedSchedule)
        val schedulesId = dao.getSchedulesId(primaryKeyNamedSchedule)
        dao.deleteSchedulesByNamedScheduleId(primaryKeyNamedSchedule)
        schedulesId.forEach { scheduleId ->
            dao.deleteEventsByScheduleId(scheduleId)
            dao.deleteEventsExtraByScheduleId(scheduleId)
        }

        if (isDefault) {
            val namedSchedules = dao.getAllNamedScheduleEntities()
            if (namedSchedules.isNotEmpty()) {
                updatePrioritySavedNamedSchedules(namedSchedules[0].id)
            }
        }
    }

    override suspend fun deleteScheduleById(
        primaryKeySchedule: Long
    ) = db.withTransaction {
        dao.deleteScheduleById(primaryKeySchedule)
        dao.deleteEventsByScheduleId(primaryKeySchedule)
        dao.deleteEventsExtraByScheduleId(primaryKeySchedule)
    }

    override suspend fun deleteEventById(
        primaryKeyEvent: Long
    ) = db.withTransaction {
        dao.deleteEventById(primaryKeyEvent)
        dao.deleteEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun deleteEventExtraByEventId(
        event: EventEntity
    ) {
        synchronizableEventExtraAction(
            event
        ) { event ->
            dao.deleteEventExtraByEventId(event.id)
        }
    }
}