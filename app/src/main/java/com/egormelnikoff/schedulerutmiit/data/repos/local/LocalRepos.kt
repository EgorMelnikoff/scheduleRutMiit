package com.egormelnikoff.schedulerutmiit.data.repos.local

import com.egormelnikoff.schedulerutmiit.data.datasource.local.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.model.News
import java.time.LocalDate

interface LocalRepos {
    suspend fun insertNamedSchedule(namedSchedule: NamedScheduleFormatted): Long
    suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    )

    suspend fun insertEvent(
        event: Event
    )

    suspend fun updateEventHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    )

    suspend fun deleteEvent(
        primaryKeyEvent: Long
    )

    suspend fun getCountEventsPerDate(
        date: LocalDate,
        scheduleId: Long
    ): Int

    suspend fun deleteNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean
    )

    suspend fun deleteSchedule(
        id: Long
    )

    suspend fun getAllNamedSchedules(): List<NamedScheduleEntity>
    suspend fun getNamedScheduleByApiId(
        apiId: String
    ): NamedScheduleFormatted?

    suspend fun getNamedScheduleById(idNamedSchedule: Long): NamedScheduleFormatted?
    suspend fun updatePriorityNamedSchedule(
        id: Long
    )

    suspend fun updatePrioritySchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    )

    suspend fun updateEventExtra(
        scheduleId: Long,
        event: Event,
        tag: Int,
        comment: String
    )

    suspend fun updateLastTimeUpdate(
        namedScheduleId: Long,
        lastTimeUpdate: Long
    )

    fun parseNews(news: News): News
}

class LocalReposImpl(
    private val namedScheduleDao: NamedScheduleDao,
    private val parser: Parser
) : LocalRepos {
    override suspend fun insertNamedSchedule(namedSchedule: NamedScheduleFormatted): Long {
        if (namedSchedule.namedScheduleEntity.isDefault) {
            namedSchedule.namedScheduleEntity.isDefault = false
        }
        val namedScheduleId = namedScheduleDao.insertNamedSchedule(namedSchedule)

        val currentCount = namedScheduleDao.getCount()
        if (currentCount == 1) {
            val namedScheduleEntityWithNewId = namedScheduleDao.getAll().first()
            namedScheduleDao.setDefaultNamedSchedule(namedScheduleEntityWithNewId.id)
        }
        return namedScheduleId
    }

    override suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ) {
        namedScheduleDao.insertScheduleWithEvents(
            namedScheduleId = namedScheduleId,
            scheduleFormatted = scheduleFormatted,
        )
    }

    override suspend fun insertEvent(event: Event) {
        namedScheduleDao.insertEvent(event)
    }

    override suspend fun updateEventHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    ) {
        namedScheduleDao.updateEventHidden(eventPrimaryKey, isHidden)
    }

    override suspend fun deleteEvent(
        primaryKeyEvent: Long
    ) {
        namedScheduleDao.deleteEventById(primaryKeyEvent)
        namedScheduleDao.deleteEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun getCountEventsPerDate(
        date: LocalDate,
        scheduleId: Long
    ): Int {
        return namedScheduleDao.getCountEventsPerDate(date.toString(), scheduleId)
    }

    override suspend fun deleteNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean
    ) {
        namedScheduleDao.delete(primaryKey)
        if (isDefault) {
            val namedSchedules = namedScheduleDao.getAll()
            if (namedSchedules.isNotEmpty()) {
                updatePriorityNamedSchedule(namedSchedules[0].id)
            }
        }
    }

    override suspend fun deleteSchedule(
        id: Long
    ) {
        namedScheduleDao.deleteScheduleById(id)
        namedScheduleDao.deleteEventsByScheduleId(id)
        namedScheduleDao.deleteEventsExtraByScheduleId(id)
    }

    override suspend fun getAllNamedSchedules(): List<NamedScheduleEntity> {
        return namedScheduleDao.getAll()
    }

    override suspend fun getNamedScheduleByApiId(apiId: String): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleByApiId(apiId.toInt())
    }

    override suspend fun getNamedScheduleById(idNamedSchedule: Long): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleById(idNamedSchedule)
    }

    override suspend fun updatePriorityNamedSchedule(id: Long) {
        namedScheduleDao.setDefaultNamedSchedule(id)
        namedScheduleDao.setNonDefaultNamedSchedule(id)
    }

    override suspend fun updatePrioritySchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    ) {
        namedScheduleDao.setDefaultSchedule(primaryKeySchedule)
        namedScheduleDao.setNonDefaultSchedule(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            primaryKeySchedule = primaryKeySchedule
        )
    }

    override suspend fun updateLastTimeUpdate(namedScheduleId: Long, lastTimeUpdate: Long) {
        namedScheduleDao.updateLastTimeUpdate(namedScheduleId, lastTimeUpdate)
    }

    override suspend fun updateEventExtra(
        scheduleId: Long,
        event: Event,
        tag: Int,
        comment: String
    ) {
        if (comment == "" && tag == 0) {
            namedScheduleDao.deleteEventExtraByEventId(event.id)
            return
        }
        val checkEventExtra = namedScheduleDao.getEventExtraByEventId(event.id)
        if (checkEventExtra != null) {
            namedScheduleDao.updateCommentEvent(scheduleId, event.id, comment)
            namedScheduleDao.updateTagEvent(scheduleId, event.id, tag)
        } else {
            namedScheduleDao.insertEventExtraData(
                EventExtraData(
                    id = event.id,
                    scheduleId = scheduleId,
                    eventName = event.name,
                    eventStartDatetime = event.startDatetime,
                    comment = comment,
                    tag = tag
                )
            )
        }
    }

    override fun parseNews(news: News): News {
        return parser.parseNews(news)
    }
}