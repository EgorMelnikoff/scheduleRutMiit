package com.egormelnikoff.schedulerutmiit.data.repos.local

import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.data.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.repos.local.database.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.ScheduleFormatted

class LocalRepos(
    private val namedScheduleDao: NamedScheduleDao,
) {
    suspend fun insertNewNamedSchedule(namedSchedule: NamedScheduleFormatted) {
        if (namedSchedule.namedScheduleEntity.isDefault) {
            namedSchedule.namedScheduleEntity.isDefault = false
        }
        namedScheduleDao.insertNamedSchedule(namedSchedule)

        val currentCount = namedScheduleDao.getCount()
        if (currentCount == 1) {
            val namedScheduleWithNewId = namedScheduleDao.getNamedScheduleByApiId(namedSchedule.namedScheduleEntity.apiId.toInt())
            namedScheduleDao.setDefaultNamedSchedule(namedScheduleWithNewId!!.namedScheduleEntity.id)
        }
    }

    suspend fun insertSchedule (
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ) {
        namedScheduleDao.insertSchedule(
            namedScheduleId = namedScheduleId,
            scheduleFormatted = scheduleFormatted,
            insertExtraEvents = false
        )
    }

    suspend fun deleteSchedule (
        id: Long,
        deleteEventsExtra: Boolean = false
    ) {
        namedScheduleDao.deleteScheduleById(id)
        namedScheduleDao.deleteEventsByScheduleId(id)
        if (deleteEventsExtra) {
            namedScheduleDao.deleteEventsExtraByScheduleId(id)
        }
    }

    suspend fun getAll(): MutableList<NamedScheduleFormatted> {
        return namedScheduleDao.getAll()
    }
    suspend fun getNamedScheduleByApiId(apiId: String): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleByApiId(apiId.toInt())
    }
    suspend fun getCount(): Int {
        return namedScheduleDao.getCount()
    }


    suspend fun updatePriorityNamedSchedule(id: Long) {
        namedScheduleDao.setDefaultNamedSchedule(id)
        namedScheduleDao.setNonDefaultNamedSchedule(id)
    }
    suspend fun updatePrioritySchedule(idSchedule: Long, idNamedSchedule: Long) {
        namedScheduleDao.setDefaultSchedule(idSchedule)
        namedScheduleDao.setNonDefaultSchedule(idSchedule, idNamedSchedule)
    }
    suspend fun updateEventExtra(
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


    suspend fun deleteSavedSchedule(primaryKey: Long, isDefault: Boolean) {
        namedScheduleDao.delete(primaryKey)
        if (isDefault) {
            val namedSchedules = namedScheduleDao.getAll()
            if (namedSchedules.isNotEmpty()) {
                updatePriorityNamedSchedule(namedSchedules[0].namedScheduleEntity.id)
            }
        }
    }
}