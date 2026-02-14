package com.egormelnikoff.schedulerutmiit.data.repos.schedule.impl

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
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
                is Result.Error -> {
                    document
                }

                is Result.Success -> {
                    parser.parseSchedule(
                        document.data,
                        timetable,
                        currentGroup
                    )
                }
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
                    return parser.parseCurrentWeek(document.data)
                }
            }
        }
    }

    /* INSERT */
    override suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long {
        val namedScheduleId = dao.insertNamedSchedule(namedSchedule)

        val currentCount = dao.getCount()
        if (currentCount == 1) {
            val namedScheduleEntityWithNewId =
                dao.getNamedScheduleById(namedScheduleId)!!
            dao.setDefaultNamedSchedule(namedScheduleEntityWithNewId.namedScheduleEntity.id)
        }
        return namedScheduleId
    }

    override suspend fun insertSchedule(
        primaryKeySchedule: Long,
        scheduleFormatted: ScheduleFormatted
    ) {
        dao.insertSchedule(
            namedScheduleId = primaryKeySchedule,
            scheduleFormatted = scheduleFormatted,
        )
    }

    override suspend fun insertEvent(
        event: Event
    ) {
        dao.insertEvent(event)
    }

    override suspend fun insertEventExtraData(
        eventExtraData: EventExtraData
    ) {
        dao.insertEventExtraData(eventExtraData)
    }

    /* GET */
    override suspend fun getSavedNamedSchedules(): List<NamedScheduleEntity> {
        return dao.getAll()
    }

    override suspend fun getNamedScheduleById(
        primaryKeyNamedSchedule: Long
    ): NamedScheduleFormatted? {
        return dao.getNamedScheduleById(primaryKeyNamedSchedule)
    }

    override suspend fun getNamedScheduleByApiId(
        apiId: Int
    ): NamedScheduleFormatted? {
        return dao.getNamedScheduleByApiId(apiId)
    }

    override suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity? {
        return dao.getDefaultNamedScheduleEntity()
    }

    override suspend fun getEventExtraByEventId(
        primaryKeyEvent: Long
    ): EventExtraData? {
        return dao.getEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun getCountEventsPerDate(
        date: String,
        scheduleId: Long
    ): Int {
        return dao.getCountEventsPerDate(date, scheduleId)
    }

    /* UPDATE */
    override suspend fun updatePrioritySavedNamedSchedules(
        primaryKeyNamedSchedule: Long
    ) {
        dao.setDefaultNamedSchedule(primaryKeyNamedSchedule)
        dao.setNonDefaultNamedSchedule(primaryKeyNamedSchedule)
    }

    override suspend fun updatePrioritySchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    ) {
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
    ) {
        dao.updateName(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            fullName = newName,
            shortName = newName.getShortName(type)
        )
    }

    override suspend fun updateLastTimeUpdate(
        primaryKeyNamedSchedule: Long,
        lastTimeUpdate: Long
    ) {
        dao.updateLastTimeUpdate(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            lastTimeUpdate = System.currentTimeMillis()
        )
    }

    override suspend fun updateCustomEvent(event: Event) {
        dao.deleteEventById(event.id)
        dao.insertEvent(event)
    }

    override suspend fun updateEventHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    ) {
        dao.updateEventHidden(eventPrimaryKey, isHidden)
    }

    override suspend fun updateCommentEvent(
        primaryKeySchedule: Long,
        primaryKeyEvent: Long,
        comment: String
    ) {
        dao.updateCommentEvent(primaryKeySchedule, primaryKeyEvent, comment)
    }

    override suspend fun updateTagEvent(
        primaryKeySchedule: Long,
        primaryKeyEvent: Long,
        tag: Int
    ) {
        dao.updateTagEvent(primaryKeySchedule, primaryKeyEvent, tag)
    }


    /* DELETE */
    override suspend fun deleteNamedScheduleById(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) {
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
    ) {
        dao.deleteScheduleById(primaryKeySchedule)
        dao.deleteEventsByScheduleId(primaryKeySchedule)
        dao.deleteEventsExtraByScheduleId(primaryKeySchedule)
    }

    override suspend fun deleteEventById(
        primaryKeyEvent: Long
    ) {
        dao.deleteEventById(primaryKeyEvent)
        dao.deleteEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun deleteEventExtraByEventId(
        primaryKeyEvent: Long
    ) {
        dao.deleteEventExtraByEventId(primaryKeyEvent)
    }
}