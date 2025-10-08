package com.egormelnikoff.schedulerutmiit.data.repos.schedule

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.Api
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.model.NonPeriodicContent
import com.egormelnikoff.schedulerutmiit.model.PeriodicContent
import com.egormelnikoff.schedulerutmiit.model.Schedule
import com.egormelnikoff.schedulerutmiit.model.Timetable
import com.egormelnikoff.schedulerutmiit.model.TimetableType
import com.egormelnikoff.schedulerutmiit.model.Timetables
import com.egormelnikoff.schedulerutmiit.ui.screens.schedule.calculateFirstDayOfWeek
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

interface ScheduleRepos {
    suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long

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

    suspend fun getNamedSchedule(
        namedScheduleId: Long = 0,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted>

    suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        onStartUpdate: () -> Unit,
    ): Result<String>

    suspend fun isEventAddingUnavailable(
        date: LocalDate,
        scheduleId: Long
    ): Boolean
}

class ScheduleReposImpl @Inject constructor(
    private val namedScheduleDao: NamedScheduleDao,
    private val api: Api,
    private val parser: Parser
) : ScheduleRepos {
    companion object {
        val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.HOURS.toMillis(6)
        const val CUSTOM_SCHEDULE_TYPE = 3
        const val MAX_EVENTS_COUNT = 10
    }

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

    override suspend fun getNamedSchedule(
        namedScheduleId: Long,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted> {
        return try {
            when (val timetables = getTimetables(apiId = apiId, type = type)) {
                is Result.Error -> Result.Error(timetables.exception)
                is Result.Success -> {
                    val schedules = mutableListOf<Schedule>()
                    for (timetable in timetables.data.timetables) {
                        val schedule = when (type) {
                            0 -> api.getSchedule(type = "group", apiId = apiId, timetable.id)
                            1 -> api.getSchedule(type = "person", apiId = apiId, timetable.id)
                            2 -> api.getSchedule(type = "room", apiId = apiId, timetable.id)
                            else -> Response.success(null)
                        }

                        if (schedule.isSuccessful && schedule.body() != null) {
                            val fixedSchedule = fixApiIssuance(apiId, timetable, schedule.body()!!)
                            schedules.add(fixedSchedule!!)
                        } else {
                            Result.Error(NoSuchElementException())
                        }
                    }
                    if (schedules.isNotEmpty()) {
                        Result.Success(
                            migrateToNewModel(
                                id = namedScheduleId,
                                name = name,
                                apiId = apiId,
                                type = type,
                                oldModelSchedules = schedules,
                                lastTimeUpdate = System.currentTimeMillis()
                            )
                        )
                    } else {
                        Result.Success(
                            NamedScheduleFormatted(
                                namedScheduleEntity = NamedScheduleEntity(
                                    id = namedScheduleId,
                                    fullName = name,
                                    shortName = getShortName(type, name),
                                    apiId = apiId,
                                    type = type,
                                    isDefault = false,
                                    lastTimeUpdate = System.currentTimeMillis()
                                ),
                                schedules = mutableListOf()
                            )
                        )
                    }
                }
            }

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun getTimetables(
        apiId: String,
        type: Int
    ): Result<Timetables> {
        return try {
            val timetables = when (type) {
                0 -> api.getTimetables("group", apiId)
                1 -> api.getTimetables("person", apiId)
                2 -> api.getTimetables("room", apiId)
                else -> Response.success(null)
            }
            if (timetables.isSuccessful && timetables.body() != null) {
                Result.Success(timetables.body()!!)
            } else {
                Result.Error(NoSuchElementException())
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun isEventAddingUnavailable(date: LocalDate, scheduleId: Long): Boolean {
        return getCountEventsPerDate(date, scheduleId) >= MAX_EVENTS_COUNT
    }

    override suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        onStartUpdate: () -> Unit,
    ): Result<String> {
        if (shouldUpdateNamedSchedule(namedScheduleEntity)) {
            onStartUpdate()
            return performNamedScheduleUpdate(namedScheduleEntity)
        }
        return Result.Success("Success update")
    }

    private fun shouldUpdateNamedSchedule(namedScheduleEntity: NamedScheduleEntity): Boolean {
        val timeSinceLastUpdate =
            System.currentTimeMillis() - namedScheduleEntity.lastTimeUpdate
        return timeSinceLastUpdate > SCHEDULE_UPDATE_THRESHOLD_MS && namedScheduleEntity.type != CUSTOM_SCHEDULE_TYPE
    }

    private suspend fun performNamedScheduleUpdate(namedScheduleEntity: NamedScheduleEntity): Result<String> {
        val remoteResult = getNamedSchedule(
            namedScheduleId = namedScheduleEntity.id,
            name = namedScheduleEntity.shortName,
            apiId = namedScheduleEntity.apiId!!,
            type = namedScheduleEntity.type
        )

        return when (remoteResult) {
            is Result.Error -> {
                Result.Error(remoteResult.exception)
            }

            is Result.Success -> {
                val oldNamedSchedule = getNamedScheduleById(namedScheduleEntity.id)
                if (oldNamedSchedule != null) {
                    mergeAndUpdateSchedules(
                        oldNamedSchedule = oldNamedSchedule,
                        newNamedSchedule = remoteResult.data
                    )
                    Result.Success("Success update")
                } else {
                    Result.Error(Exception("Cannot find schedule"))
                }
            }
        }
    }


    private suspend fun mergeAndUpdateSchedules(
        oldNamedSchedule: NamedScheduleFormatted,
        newNamedSchedule: NamedScheduleFormatted
    ) {
        val oldNamedSchedulesMap =
            oldNamedSchedule.schedules.associateBy { it.scheduleEntity.timetableId }

        newNamedSchedule.schedules.forEach { updatedSchedule ->
            val oldSchedule = oldNamedSchedulesMap[updatedSchedule.scheduleEntity.timetableId]
            if (oldSchedule != null) {
                val updatedScheduleEntity =
                    if (oldSchedule.scheduleEntity.recurrence?.currentNumber != updatedSchedule.scheduleEntity.recurrence?.currentNumber) {
                        oldSchedule.scheduleEntity.copy(
                            recurrence = updatedSchedule.scheduleEntity.recurrence
                        )
                    } else {
                        oldSchedule.scheduleEntity
                    }

                val updatedEvents = updatedSchedule.events.map { event ->
                    val oldEvent = oldSchedule.events.find { it.customEquals(event) }
                    event.copy(
                        id = oldEvent?.id ?: 0L,
                        isHidden = oldEvent?.isHidden ?: false
                    )
                }

                val updatedScheduleWithId = ScheduleFormatted(
                    scheduleEntity = updatedScheduleEntity,
                    events = updatedEvents,
                    eventsExtraData = oldSchedule.eventsExtraData
                )
                deleteSchedule(oldSchedule.scheduleEntity.id)
                insertSchedule(
                    oldNamedSchedule.namedScheduleEntity.id,
                    updatedScheduleWithId
                )

            } else {
                insertSchedule(oldNamedSchedule.namedScheduleEntity.id, updatedSchedule)
            }
        }

        oldNamedSchedule.schedules.forEach { oldSchedule ->
            val stillExists =
                newNamedSchedule.schedules.any { it.scheduleEntity.timetableId == oldSchedule.scheduleEntity.timetableId }
            val isOutdated = LocalDate.now() > oldSchedule.scheduleEntity.endDate
            if (!stillExists && isOutdated) {
                deleteSchedule(
                    id = oldSchedule.scheduleEntity.id
                )
            }
        }
        updateLastTimeUpdate(
            oldNamedSchedule.namedScheduleEntity.id,
            System.currentTimeMillis()
        )
    }

    private suspend fun fixApiIssuance(
        apiId: String,
        timetable: Timetable,
        schedule: Schedule
    ): Schedule? {
        val fixedSchedule = when (timetable.type) {
            TimetableType.PERIODIC.type -> {
                Schedule(
                    timetable = timetable,
                    nonPeriodicContent = null,
                    periodicContent = if (schedule.periodicContent != null) {
                        schedule.periodicContent
                    } else {
                        val clearedEvents = schedule.nonPeriodicContent?.events
                            ?.filter { it.startDatetime != null && it.name != null }
                            ?.distinctBy {
                                it.hashCode()
                            }
                            ?: emptyList()

                        val weekFields = WeekFields.ISO
                        val eventsByWeek = clearedEvents.groupBy {
                            it.startDatetime?.get(weekFields.weekOfYear())
                        }
                        val weeksIndexes = eventsByWeek.keys.toList()

                        val checkedEvents = mutableListOf<Event>()
                        for ((weekIndex, eventsInWeek) in eventsByWeek) {
                            for (event in eventsInWeek) {
                                val updatedEvent = event.copy(
                                    timeSlotName = getTimeslotName(event.startDatetime!!.toLocalTime()),
                                    periodNumber = (weekIndex!! % weeksIndexes.size + 1)
                                )
                                checkedEvents.add(updatedEvent)
                            }
                        }
                        PeriodicContent(
                            events = checkedEvents,
                            recurrence = Recurrence(
                                frequency = "WEEKLY",
                                currentNumber = parser.parseCurrentWeek("https://www.miit.ru/timetable/$apiId"),
                                interval = weeksIndexes.size,
                                firstWeekNumber = 1
                            )
                        )
                    }
                )
            }

            TimetableType.NON_PERIODIC.type, TimetableType.SESSION.type -> {
                Schedule(
                    timetable = timetable,
                    periodicContent = null,
                    nonPeriodicContent = NonPeriodicContent(
                        events = schedule.nonPeriodicContent?.events
                            ?: schedule.periodicContent?.events
                    )
                )
            }

            else -> null
        }
        return fixedSchedule
    }

    private fun migrateToNewModel(
        id: Long,
        name: String,
        apiId: String,
        type: Int,
        lastTimeUpdate: Long,
        oldModelSchedules: MutableList<Schedule>
    ): NamedScheduleFormatted {
        val schedulesFormatted = mutableListOf<ScheduleFormatted>()
        oldModelSchedules.forEachIndexed { index, oldSchedule ->
            val events = mutableListOf<Event>()
            oldSchedule.periodicContent?.events
                ?.filter { it.startDatetime != null }
                ?.let { events.addAll(it) }
            oldSchedule.nonPeriodicContent?.events
                ?.filter { it.startDatetime != null }
                ?.let { events.addAll(it) }

            if (events.isNotEmpty()) {
                val today = LocalDate.now()
                val scheduleEntity = ScheduleEntity(
                    namedScheduleId = id,
                    startDate = oldSchedule.timetable?.startDate!!,
                    endDate = oldSchedule.timetable.endDate!!,
                    recurrence = if (oldSchedule.periodicContent != null) {
                        if (today > oldSchedule.timetable.startDate) {
                            val currentWeekIndex = abs(
                                ChronoUnit.WEEKS.between(
                                    calculateFirstDayOfWeek(oldSchedule.timetable.startDate),
                                    calculateFirstDayOfWeek(LocalDate.now())
                                )
                            ).plus(1)
                            val firstWeekNumber =
                                ((currentWeekIndex + oldSchedule.periodicContent.recurrence!!.currentNumber!!)
                                        % oldSchedule.periodicContent.recurrence.interval!!)
                                    .toInt()
                                    .plus(1)
                            Recurrence(
                                frequency = oldSchedule.periodicContent.recurrence.frequency,
                                interval = oldSchedule.periodicContent.recurrence.interval,
                                currentNumber = oldSchedule.periodicContent.recurrence.currentNumber,
                                firstWeekNumber = firstWeekNumber
                            )
                        } else {
                            Recurrence(
                                frequency = oldSchedule.periodicContent.recurrence!!.frequency,
                                interval = oldSchedule.periodicContent.recurrence.interval,
                                currentNumber = oldSchedule.periodicContent.recurrence.currentNumber,
                                firstWeekNumber = oldSchedule.periodicContent.recurrence.currentNumber
                                    ?: 1
                            )
                        }
                    } else null,
                    typeName = oldSchedule.timetable.typeName!!,
                    downloadUrl = oldSchedule.timetable.downloadUrl!!,
                    startName = oldSchedule.timetable.name!!,
                    timetableId = oldSchedule.timetable.id!!,
                    isDefault = index == 0
                )
                schedulesFormatted.add(
                    ScheduleFormatted(
                        scheduleEntity = scheduleEntity,
                        events = events,
                        eventsExtraData = mutableListOf()
                    )
                )
            }
        }
        val namedScheduleFormatted = NamedScheduleFormatted(
            namedScheduleEntity = NamedScheduleEntity(
                id = id,
                fullName = name,
                shortName = getShortName(type, name),
                apiId = apiId,
                type = type,
                isDefault = false,
                lastTimeUpdate = lastTimeUpdate
            ),
            schedules = schedulesFormatted,
        )

        return namedScheduleFormatted
    }

    private fun getShortName(type: Int, fullName: String): String {
        return if (type == 0 || type == 2) {
            fullName
        } else {
            val nameParts = fullName.split(" ")
            if (nameParts.size == 3) {
                "${nameParts.first()} ${nameParts[1][0]}. ${nameParts[2][0]}."
            } else {
                fullName
            }
        }
    }

    private fun getTimeslotName(time: LocalTime): String? {
        return when {
            (time.hour == 5 && time.minute == 30) -> "1 пара"
            (time.hour == 7 && time.minute == 5) -> "2 пара"
            (time.hour == 8 && time.minute == 40) -> "3 пара"
            (time.hour == 10 && time.minute == 45) -> "4 пара"
            (time.hour == 12 && time.minute == 20) -> "5 пара"
            (time.hour == 13 && time.minute == 55) -> "6 пара"
            (time.hour == 15 && time.minute == 30) -> "7 пара"
            (time.hour == 17 && time.minute == 0) -> "8 пара"
            (time.hour == 18 && time.minute == 35) -> "9 пара"
            (time.hour == 20 && time.minute == 10) -> "10 пара"
            else -> null
        }
    }
}