package com.egormelnikoff.schedulerutmiit.data.repos.local

import com.egormelnikoff.schedulerutmiit.data.datasource.local.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.model.News

interface LocalReposInterface {
    suspend fun insertNamedSchedule(namedSchedule: NamedScheduleFormatted)
    suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    )

    suspend fun deleteNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean
    )

    suspend fun deleteSchedule(
        id: Long,
        deleteEventsExtra: Boolean = false
    )

    suspend fun getCount(): Int
    suspend fun getAllNamedSchedules(): MutableList<NamedScheduleFormatted>
    suspend fun getNamedScheduleByApiId(
        apiId: String
    ): NamedScheduleFormatted?

    suspend fun getNamedScheduleById(idNamedSchedule: Long): NamedScheduleFormatted?
    suspend fun updatePriorityNamedSchedule(
        id: Long
    )

    suspend fun updatePrioritySchedule(
        idSchedule: Long,
        idNamedSchedule: Long
    )

    suspend fun updateEventExtra(
        scheduleId: Long,
        event: Event,
        tag: Int,
        comment: String
    )

    fun parseNews(news: News): News
}

class LocalRepos(
    private val namedScheduleDao: NamedScheduleDao,
    private val parser: Parser
) : LocalReposInterface {
    override suspend fun insertNamedSchedule(namedSchedule: NamedScheduleFormatted) {
        if (namedSchedule.namedScheduleEntity.isDefault) {
            namedSchedule.namedScheduleEntity.isDefault = false
        }
        namedScheduleDao.insertNamedSchedule(namedSchedule)

        val currentCount = namedScheduleDao.getCount()
        if (currentCount == 1) {
            val namedScheduleWithNewId = namedScheduleDao.getAll().first()
            namedScheduleDao.setDefaultNamedSchedule(namedScheduleWithNewId.namedScheduleEntity.id)
        }
    }

    override suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ) {
        namedScheduleDao.insertSchedule(
            namedScheduleId = namedScheduleId,
            scheduleFormatted = scheduleFormatted,
            insertExtraEvents = false
        )
    }

    override suspend fun deleteNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean
    ) {
        namedScheduleDao.delete(primaryKey)
        if (isDefault) {
            val namedSchedules = namedScheduleDao.getAll()
            if (namedSchedules.isNotEmpty()) {
                updatePriorityNamedSchedule(namedSchedules[0].namedScheduleEntity.id)
            }
        }
    }

    override suspend fun deleteSchedule(
        id: Long,
        deleteEventsExtra: Boolean
    ) {
        namedScheduleDao.deleteScheduleById(id)
        namedScheduleDao.deleteEventsByScheduleId(id)
        if (deleteEventsExtra) {
            namedScheduleDao.deleteEventsExtraByScheduleId(id)
        }
    }

    override suspend fun getAllNamedSchedules(): MutableList<NamedScheduleFormatted> {
        return namedScheduleDao.getAll()
    }

    override suspend fun getNamedScheduleByApiId(apiId: String): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleByApiId(apiId.toInt())
    }

    override suspend fun getNamedScheduleById(idNamedSchedule: Long): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleById(idNamedSchedule)
    }

    override suspend fun getCount(): Int {
        return namedScheduleDao.getCount()
    }

    override suspend fun updatePriorityNamedSchedule(id: Long) {
        namedScheduleDao.setDefaultNamedSchedule(id)
        namedScheduleDao.setNonDefaultNamedSchedule(id)
    }

    override suspend fun updatePrioritySchedule(idSchedule: Long, idNamedSchedule: Long) {
        namedScheduleDao.setDefaultSchedule(idSchedule)
        namedScheduleDao.setNonDefaultSchedule(idSchedule, idNamedSchedule)
    }

    override suspend fun updateEventExtra(
        scheduleId: Long,
        event: Event,
        tag: Int,
        comment: String
    ) {
        if (comment == "" && tag == 0) {
            namedScheduleDao.deleteEventsExtraByEventId(event.id)
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