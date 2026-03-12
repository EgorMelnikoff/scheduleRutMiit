package com.egormelnikoff.schedulerutmiit.data.repos.schedule.impl

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.extension.getShortName
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetable
import com.egormelnikoff.schedulerutmiit.app.model.Timetables
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.Dao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.NetworkHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class ScheduleReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val parser: Parser,
    private val networkHelper: NetworkHelper,
    private val dao: Dao
) : ScheduleRepos {
    /* FETCH */
    override suspend fun fetchTimetables(
        apiId: Int,
        type: NamedScheduleType
    ): Result<Timetables> {
        return networkHelper.callNetwork(
            requestType = "Timetables",
            requestParams = "Type: $type; ApiId: $apiId",
            timeoutMs = 5000,
            callApi = {
                miitApi.getTimetables(type.typeName, apiId)
            },
            callParser = null
        )
    }

    override suspend fun fetchScheduleApi(
        namedScheduleType: NamedScheduleType,
        apiId: String,
        timetableId: String
    ): Result<Schedule> {
        return networkHelper.callNetwork(
            requestType = "Schedule",
            requestParams = "Type: ${namedScheduleType}; ApiId: $apiId; TimetableId: $timetableId",
            timeoutMs = 10000,
            callApi = {
                miitApi.getSchedule(
                    namedScheduleType.typeName,
                    apiId,
                    timetableId
                )
            },
            callParser = null
        )
    }

    override suspend fun fetchScheduleParser(
        namedScheduleType: NamedScheduleType,
        name: String,
        apiId: Int,
        timetable: Timetable,
        currentGroup: Group?
    ): Result<Schedule> {
        networkHelper.callNetwork(
            requestType = "ScheduleParser",
            requestParams = "Id: $apiId; Start date: ${timetable.startDate}",
            timeoutMs = 10000,
            callParser = {
                Jsoup.connect(
                    Endpoints.scheduleUrl(
                        namedScheduleType,
                        apiId,
                        timetable.startDate.toString(),
                        timetable.type.id.toString()
                    )
                ).get()
            },
            callApi = null
        ).let { document ->
            return when (document) {
                is Result.Error -> document

                is Result.Success -> Result.Success(
                    parser.parseSchedule(
                        document.data,
                        timetable,
                        currentGroup
                    )
                )
            }
        }
    }

    override suspend fun fetchCurrentWeek(
        namedScheduleType: NamedScheduleType,
        apiId: Int,
        startDate: String,
        type: String
    ): Int {
        networkHelper.callNetwork(
            requestType = "CurrentWeek",
            requestParams = "id: $apiId",
            callParser = {
                Jsoup.connect(
                    Endpoints.scheduleUrl(
                        namedScheduleType, apiId, startDate, type
                    )
                ).get()
            },
            callApi = null
        ).let { document ->
            return when (document) {
                is Result.Error -> 1

                is Result.Success -> {
                    parser.parseCurrentWeek(document.data)
                }
            }
        }
    }

    /* INSERT */
    override suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long = withContext(Dispatchers.IO) {
        val namedScheduleId = dao.insertNamedSchedule(namedSchedule)

        val currentCount = dao.getCount()
        if (currentCount == 1) {
            val namedScheduleEntityWithNewId =
                dao.getNamedScheduleById(namedScheduleId)!!
            dao.setDefaultNamedSchedule(namedScheduleEntityWithNewId.namedScheduleEntity.id)
        }
        return@withContext namedScheduleId
    }

    override suspend fun insertSchedule(
        primaryKeySchedule: Long,
        scheduleFormatted: ScheduleFormatted
    ) = withContext(Dispatchers.IO) {
        dao.insertSchedule(
            namedScheduleId = primaryKeySchedule,
            scheduleFormatted = scheduleFormatted,
        )
    }

    override suspend fun replaceSchedule(
        oldScheduleId: Long,
        namedScheduleId: Long,
        newScheduleFormatted: ScheduleFormatted
    ) = withContext(Dispatchers.IO) {
        dao.replaceSchedule(oldScheduleId, namedScheduleId, newScheduleFormatted)
    }

    override suspend fun insertEvent(
        event: Event
    ) = withContext(Dispatchers.IO) {
        dao.insertEvent(event)
    }

    override suspend fun insertEventExtraData(
        eventExtraData: EventExtraData
    ) = withContext(Dispatchers.IO) {
        dao.insertEventExtraData(eventExtraData)
    }

    /* GET */
    override suspend fun getSavedNamedSchedules(): List<NamedScheduleEntity> =
        withContext(Dispatchers.IO) {
            return@withContext dao.getAll()
        }

    override suspend fun getNamedScheduleById(
        primaryKeyNamedSchedule: Long
    ): NamedScheduleFormatted? = withContext(Dispatchers.IO) {
        return@withContext dao.getNamedScheduleById(primaryKeyNamedSchedule)
    }

    override suspend fun getNamedScheduleByApiId(
        apiId: Int
    ): NamedScheduleFormatted? = withContext(Dispatchers.IO) {
        return@withContext dao.getNamedScheduleByApiId(apiId)
    }

    override suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity? =
        withContext(Dispatchers.IO) {
            return@withContext dao.getDefaultNamedScheduleEntity()
        }

    override suspend fun getEventExtraByEventId(
        primaryKeyEvent: Long
    ): EventExtraData? = withContext(Dispatchers.IO) {
        return@withContext dao.getEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun getCountEventsPerDate(
        date: String,
        scheduleId: Long
    ): Int = withContext(Dispatchers.IO) {
        return@withContext dao.getCountEventsPerDate(date, scheduleId)
    }

    /* UPDATE */
    override suspend fun updatePrioritySavedNamedSchedules(
        primaryKeyNamedSchedule: Long
    ) = withContext(Dispatchers.IO) {
        dao.setDefaultNamedSchedule(primaryKeyNamedSchedule)
        dao.setNonDefaultNamedSchedule(primaryKeyNamedSchedule)
    }

    override suspend fun updatePrioritySchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    ) = withContext(Dispatchers.IO) {
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
    ) = withContext(Dispatchers.IO) {
        dao.updateName(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            fullName = newName,
            shortName = newName.getShortName(type)
        )
    }

    override suspend fun updateLastTimeUpdate(
        primaryKeyNamedSchedule: Long
    ) = withContext(Dispatchers.IO) {
        dao.updateLastTimeUpdate(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            lastTimeUpdate = System.currentTimeMillis()
        )
    }

    override suspend fun updateCustomEvent(event: Event) = withContext(Dispatchers.IO) {
        dao.deleteEventById(event.id)
        dao.insertEvent(event)
    }

    override suspend fun updateEventHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    ) = withContext(Dispatchers.IO) {
        dao.updateEventHidden(eventPrimaryKey, isHidden)
    }

    override suspend fun updateCommentEvent(
        primaryKeySchedule: Long,
        primaryKeyEvent: Long,
        comment: String
    ) = withContext(Dispatchers.IO) {
        dao.updateCommentEvent(primaryKeySchedule, primaryKeyEvent, comment)
    }

    override suspend fun updateTagEvent(
        primaryKeySchedule: Long,
        primaryKeyEvent: Long,
        tag: Int
    ) = withContext(Dispatchers.IO) {
        dao.updateTagEvent(primaryKeySchedule, primaryKeyEvent, tag)
    }


    /* DELETE */
    override suspend fun deleteNamedScheduleById(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) = withContext(Dispatchers.IO) {
        dao.delete(primaryKeyNamedSchedule)
        if (isDefault) {
            val namedSchedules = dao.getAll()
            if (namedSchedules.isNotEmpty()) {
                updatePrioritySavedNamedSchedules(namedSchedules[0].id)
            }
        }
    }

    override suspend fun deleteScheduleById(
        primaryKeySchedule: Long
    ) = withContext(Dispatchers.IO) {
        dao.deleteScheduleById(primaryKeySchedule)
        dao.deleteEventsByScheduleId(primaryKeySchedule)
        dao.deleteEventsExtraByScheduleId(primaryKeySchedule)
    }

    override suspend fun deleteEventById(
        primaryKeyEvent: Long
    ) = withContext(Dispatchers.IO) {
        dao.deleteEventById(primaryKeyEvent)
        dao.deleteEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun deleteEventExtraByEventId(
        primaryKeyEvent: Long
    ) = withContext(Dispatchers.IO) {
        dao.deleteEventExtraByEventId(primaryKeyEvent)
    }
}