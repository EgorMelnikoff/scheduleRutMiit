package com.egormelnikoff.schedulerutmiit.data.repos.schedule

import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.NonPeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.PeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.Recurrence
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.Timetable
import com.egormelnikoff.schedulerutmiit.app.model.TimetableType
import com.egormelnikoff.schedulerutmiit.app.model.Timetables
import com.egormelnikoff.schedulerutmiit.app.model.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
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

    suspend fun deleteSavedEvent(
        primaryKeyEvent: Long
    )

    suspend fun deleteSavedNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean
    )

    suspend fun deleteSavedSchedule(
        id: Long
    )

    suspend fun getAllSavedNamedSchedules(): List<NamedScheduleEntity>
    suspend fun getSavedNamedScheduleByApiId(
        apiId: String
    ): NamedScheduleFormatted?

    suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity?

    suspend fun getSavedNamedScheduleById(idNamedSchedule: Long): NamedScheduleFormatted?
    suspend fun updatePrioritySavedNamedSchedules(
        id: Long
    )

    suspend fun updatePrioritySavedSchedules(
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

    suspend fun getNewNamedSchedule(
        namedScheduleId: Long = 0,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted>

    suspend fun updateSavedNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        onStartUpdate: () -> Unit
    ): Result<String>

    suspend fun isEventAddingUnavailable(
        date: LocalDate,
        scheduleId: Long
    ): Boolean
}

class ScheduleReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val miitApiHelper: MiitApiHelper,
    private val parser: Parser,
    private val namedScheduleDao: NamedScheduleDao
) : ScheduleRepos {
    companion object {
        val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.HOURS.toMillis(10)
        const val CUSTOM_SCHEDULE_TYPE = 3
        const val MAX_EVENTS_COUNT = 10
    }

    override suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long {
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

    override suspend fun deleteSavedEvent(
        primaryKeyEvent: Long
    ) {
        namedScheduleDao.deleteEventById(primaryKeyEvent)
        namedScheduleDao.deleteEventExtraByEventId(primaryKeyEvent)
    }

    override suspend fun deleteSavedNamedSchedule(
        primaryKey: Long,
        isDefault: Boolean
    ) {
        namedScheduleDao.delete(primaryKey)
        if (isDefault) {
            val namedSchedules = namedScheduleDao.getAll()
            if (namedSchedules.isNotEmpty()) {
                updatePrioritySavedNamedSchedules(namedSchedules[0].id)
            }
        }
    }

    override suspend fun deleteSavedSchedule(
        id: Long
    ) {
        namedScheduleDao.deleteScheduleById(id)
        namedScheduleDao.deleteEventsByScheduleId(id)
        namedScheduleDao.deleteEventsExtraByScheduleId(id)
    }

    override suspend fun getAllSavedNamedSchedules(): List<NamedScheduleEntity> {
        return namedScheduleDao.getAll()
    }

    override suspend fun getSavedNamedScheduleByApiId(apiId: String): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleByApiId(apiId.toInt())
    }

    override suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity? {
        return namedScheduleDao.getDefaultNamedScheduleEntity()
    }

    override suspend fun getSavedNamedScheduleById(
        idNamedSchedule: Long
    ): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleById(idNamedSchedule)
    }


    override suspend fun updatePrioritySavedNamedSchedules(id: Long) {
        namedScheduleDao.setDefaultNamedSchedule(id)
        namedScheduleDao.setNonDefaultNamedSchedule(id)
    }

    override suspend fun updatePrioritySavedSchedules(
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

    override suspend fun getNewNamedSchedule(
        namedScheduleId: Long,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted> {
        return when (val timetables = getTimetables(apiId = apiId, type = type)) {
            is Result.Error -> timetables
            is Result.Success -> {
                val schedules = mutableListOf<Schedule>()
                for (timetable in timetables.data.timetables) {
                    val schedule = miitApiHelper.callApiWithExceptions(
                        type = "Schedule ($name $apiId $type)"
                    ) {
                        when (type) {
                            0 -> miitApi.getSchedule(
                                type = "group",
                                apiId = apiId,
                                timetableId = timetable.id
                            )

                            1 -> miitApi.getSchedule(
                                type = "person",
                                apiId = apiId,
                                timetableId = timetable.id
                            )

                            2 -> miitApi.getSchedule(
                                type = "room",
                                apiId = apiId,
                                timetableId = timetable.id
                            )

                            else -> Response.success(null)
                        }
                    }
                    when (schedule) {
                        is Result.Success -> {
                            schedules.add(schedule.data.fixApiIssuance(apiId, timetable))
                        }

                        is Result.Error -> {
                            Result.Error(schedule.error)
                        }
                    }
                }
                Result.Success(
                    schedules.migrateToNewModel(
                        id = namedScheduleId,
                        name = name,
                        apiId = apiId,
                        type = type,
                        lastTimeUpdate = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private suspend fun getTimetables(
        apiId: String,
        type: Int
    ): Result<Timetables> {
        return miitApiHelper.callApiWithExceptions(
            type = "Timetables $apiId"
        ) {
            when (type) {
                0 -> miitApi.getTimetables("group", apiId)
                1 -> miitApi.getTimetables("person", apiId)
                2 -> miitApi.getTimetables("room", apiId)
                else -> Response.success(null)
            }
        }
    }

    override suspend fun isEventAddingUnavailable(date: LocalDate, scheduleId: Long): Boolean {
        return getCountEventsPerDate(date, scheduleId) >= MAX_EVENTS_COUNT
    }

    private suspend fun getCountEventsPerDate(
        date: LocalDate,
        scheduleId: Long
    ): Int {
        return namedScheduleDao.getCountEventsPerDate(date.toString(), scheduleId)
    }

    override suspend fun updateSavedNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        onStartUpdate: () -> Unit
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

    private suspend fun performNamedScheduleUpdate(
        namedScheduleEntity: NamedScheduleEntity
    ): Result<String> {
        val remoteResult = getNewNamedSchedule(
            namedScheduleId = namedScheduleEntity.id,
            name = namedScheduleEntity.shortName,
            apiId = namedScheduleEntity.apiId!!,
            type = namedScheduleEntity.type
        )

        return when (remoteResult) {
            is Result.Error -> {
                Result.Error(remoteResult.error)
            }

            is Result.Success -> {
                val oldNamedSchedule = getSavedNamedScheduleById(namedScheduleEntity.id)
                if (oldNamedSchedule != null) {
                    mergeAndUpdateSchedules(
                        oldNamedSchedule = oldNamedSchedule,
                        newNamedSchedule = remoteResult.data
                    )
                    Result.Success("Success update")
                } else {
                    Result.Error(Error.UnexpectedError(Exception("Cannot find schedule")))
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
                deleteSavedSchedule(oldSchedule.scheduleEntity.id)
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
                deleteSavedSchedule(
                    id = oldSchedule.scheduleEntity.id
                )
            }
        }
        updateLastTimeUpdate(
            oldNamedSchedule.namedScheduleEntity.id,
            System.currentTimeMillis()
        )
    }

    private suspend fun Schedule.fixApiIssuance(
        apiId: String,
        timetable: Timetable
    ): Schedule {
        val fixedSchedule = when (timetable.type) {
            TimetableType.NON_PERIODIC.type, TimetableType.SESSION.type -> {
                Schedule(
                    timetable = timetable,
                    periodicContent = null,
                    nonPeriodicContent = NonPeriodicContent(
                        events = this.nonPeriodicContent?.events
                            ?: this.periodicContent?.events
                    )
                )
            }

            else -> {
                Schedule(
                    timetable = timetable,
                    nonPeriodicContent = null,
                    periodicContent = if (this.periodicContent != null) {
                        this.periodicContent
                    } else {
                        val clearedEvents = this.nonPeriodicContent?.events
                            ?.filter { it.startDatetime != null && it.name != null }
                            ?.distinctBy {
                                it.customHashCode()
                            }
                            ?: emptyList()

                        val eventsByWeek = clearedEvents.groupBy {
                            it.startDatetime?.get(WeekFields.ISO.weekOfYear())
                        }
                        val weeksIndexes = eventsByWeek.keys.toList()

                        val checkedEvents = eventsByWeek.flatMap { (weekIndex, eventsInWeek) ->
                            eventsInWeek.map { event ->
                                event.copy(
                                    timeSlotName = event.startDatetime!!.getTimeslotName(),
                                    periodNumber = (weekIndex!! % weeksIndexes.size + 1)
                                )
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
        }
        return fixedSchedule
    }

    private fun MutableList<Schedule>.migrateToNewModel(
        id: Long,
        name: String,
        apiId: String,
        type: Int,
        lastTimeUpdate: Long
    ): NamedScheduleFormatted {
        val namedScheduleEntity = NamedScheduleEntity(
            id = id,
            fullName = name,
            shortName = name.getShortName(type),
            apiId = apiId,
            type = type,
            isDefault = false,
            lastTimeUpdate = lastTimeUpdate
        )
        if (this.isEmpty()) {
            return NamedScheduleFormatted(
                namedScheduleEntity = namedScheduleEntity,
                schedules = listOf(),
            )
        }
        val schedulesFormatted = mutableListOf<ScheduleFormatted>()
        this.forEachIndexed { index, oldSchedule ->
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
                                    oldSchedule.timetable.startDate.calculateFirstDayOfWeek(),
                                    LocalDate.now().calculateFirstDayOfWeek()
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
        return NamedScheduleFormatted(
            namedScheduleEntity = namedScheduleEntity,
            schedules = schedulesFormatted,
        )
    }

    private fun String.getShortName(type: Int): String {
        return if (type == 1) {
            val nameParts = this.split(" ")
            if (nameParts.size == 3) {
                "${nameParts.first()} ${nameParts[1][0]}. ${nameParts[2][0]}."
            } else {
                this
            }
        } else {
            this
        }
    }

    private fun LocalDateTime.getTimeslotName(): String? {
        return when {
            (this.hour == 5 && this.minute == 30) -> "1 пара"
            (this.hour == 7 && this.minute == 5) -> "2 пара"
            (this.hour == 8 && this.minute == 40) -> "3 пара"
            (this.hour == 10 && this.minute == 45) -> "4 пара"
            (this.hour == 12 && this.minute == 20) -> "5 пара"
            (this.hour == 13 && this.minute == 55) -> "6 пара"
            (this.hour == 15 && this.minute == 30) -> "7 пара"
            (this.hour == 17 && this.minute == 0) -> "8 пара"
            (this.hour == 18 && this.minute == 35) -> "9 пара"
            (this.hour == 20 && this.minute == 10) -> "10 пара"
            else -> null
        }
    }
}