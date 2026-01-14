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
import com.egormelnikoff.schedulerutmiit.app.model.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.model.getTimeSlotName
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.datasource.local.database.NamedScheduleDao
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.exception.ScheduleLoadException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

interface ScheduleRepos {
    suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long

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
        primaryKeyNamedSchedule: Long,
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

    suspend fun getSavedNamedScheduleById(
        primaryKeyNamedSchedule: Long
    ): NamedScheduleFormatted?

    suspend fun updatePrioritySavedNamedSchedules(
        id: Long
    )

    suspend fun renameNamedSchedule(
        primaryKeyNamedSchedule: Long,
        type: Int,
        newName: String
    )

    suspend fun updatePrioritySavedSchedules(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    )

    suspend fun updateEventExtra(
        primaryKeySchedule: Long,
        event: Event,
        tag: Int,
        comment: String
    )

    suspend fun getNewNamedSchedule(
        primaryKeyNamedSchedule: Long = 0,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted>

    suspend fun updateSavedNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        onStartUpdate: (() -> Unit)? = null,
        onFakeUpdating: (suspend () -> Unit)? = null
    ): Result<String>

    suspend fun isEventAddingUnavailable(
        date: LocalDate,
        scheduleId: Long
    ): Boolean
}

class ScheduleReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val apiHelper: ApiHelper,
    private val parser: Parser,
    private val namedScheduleDao: NamedScheduleDao
) : ScheduleRepos {
    companion object {
        private val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.HOURS.toMillis(10)
        const val CUSTOM_SCHEDULE_TYPE = 3
        const val MAX_EVENTS_COUNT = 10
    }

    override suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long {
        val namedScheduleId = namedScheduleDao.insertNamedSchedule(namedSchedule)

        val currentCount = namedScheduleDao.getCount()
        if (currentCount == 1) {
            val namedScheduleEntityWithNewId = namedScheduleDao.getAll().first()
            namedScheduleDao.setDefaultNamedSchedule(namedScheduleEntityWithNewId.id)
        }
        return namedScheduleId
    }

    private suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ) {
        namedScheduleDao.insertSchedule(
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
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    ) {
        namedScheduleDao.delete(primaryKeyNamedSchedule)
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
        primaryKeyNamedSchedule: Long
    ): NamedScheduleFormatted? {
        return namedScheduleDao.getNamedScheduleById(primaryKeyNamedSchedule)
    }


    override suspend fun updatePrioritySavedNamedSchedules(id: Long) {
        namedScheduleDao.setDefaultNamedSchedule(id)
        namedScheduleDao.setNonDefaultNamedSchedule(id)
    }

    override suspend fun renameNamedSchedule(
        primaryKeyNamedSchedule: Long,
        type: Int,
        newName: String
    ) {
        namedScheduleDao.updateName(
            primaryKeyNamedSchedule = primaryKeyNamedSchedule,
            fullName = newName,
            shortName = newName.getShortName(type)
        )
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

    override suspend fun updateEventExtra(
        primaryKeySchedule: Long,
        event: Event,
        tag: Int,
        comment: String
    ) {
        if (comment == "" && tag == 0) {
            namedScheduleDao.deleteEventExtraByEventId(event.id)
            return
        }
        val eventExtraData = namedScheduleDao.getEventExtraByEventId(event.id)

        eventExtraData?.let {
            namedScheduleDao.updateCommentEvent(primaryKeySchedule, event.id, comment)
            namedScheduleDao.updateTagEvent(primaryKeySchedule, event.id, tag)
        } ?: namedScheduleDao.insertEventExtraData(
            EventExtraData(
                id = event.id,
                scheduleId = primaryKeySchedule,
                eventName = event.name,
                eventStartDatetime = event.startDatetime,
                comment = comment,
                tag = tag
            )
        )
    }

    override suspend fun getNewNamedSchedule(
        primaryKeyNamedSchedule: Long,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted> = coroutineScope {

        when (val timetables = getTimetables(apiId = apiId, type = type)) {
            is Result.Error -> Result.Error(timetables.typedError)

            is Result.Success -> {
                if (timetables.data.timetables.isEmpty()) {
                    return@coroutineScope Result.Error(TypedError.EmptyBodyError)
                }

                val deferredSchedules = timetables.data.timetables.map { timetable ->
                    async {
                        apiHelper.callApiWithExceptions(
                            fetchDataType = "Schedule",
                            message = "Type: $type; ApiId: $apiId; TimetableId: ${timetable.id}"
                        ) {
                            miitApi.getSchedule(type.getApiTypeById(), apiId, timetable.id)
                        }.let { result ->
                            when (result) {
                                is Result.Success -> result.data.fixApiIssuance(apiId, timetable)

                                is Result.Error -> {
                                    throw ScheduleLoadException(result.typedError)
                                }
                            }
                        }
                    }
                }

                return@coroutineScope try {
                    val schedules = deferredSchedules.awaitAll()
                    Result.Success(
                        schedules.migrateToNewModel(
                            id = primaryKeyNamedSchedule,
                            name = name,
                            apiId = apiId,
                            type = type,
                            lastTimeUpdate = System.currentTimeMillis()
                        )
                    )
                } catch (e: ScheduleLoadException) {
                    Result.Error(e.error)
                }
            }
        }
    }

    private suspend fun getTimetables(
        apiId: String,
        type: Int
    ): Result<Timetables> {
        return apiHelper.callApiWithExceptions(
            fetchDataType = "Timetables",
            message = "Type: $type; ApiId: $apiId"
        ) {
            miitApi.getTimetables(type.getApiTypeById(), apiId)
        }
    }

    override suspend fun isEventAddingUnavailable(date: LocalDate, scheduleId: Long): Boolean {
        val eventsCount = namedScheduleDao.getCountEventsPerDate(date.toString(), scheduleId)
        return eventsCount >= MAX_EVENTS_COUNT
    }

    override suspend fun updateSavedNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity,
        onStartUpdate: (() -> Unit)?,
        onFakeUpdating: (suspend () -> Unit)?
    ): Result<String> {
        return if (shouldUpdateNamedSchedule(namedScheduleEntity)) {
            onStartUpdate?.invoke()
            performNamedScheduleUpdate(namedScheduleEntity)
        } else {
            onFakeUpdating?.invoke()
            Result.Success("No schedule update required")
        }
    }

    private fun shouldUpdateNamedSchedule(namedScheduleEntity: NamedScheduleEntity): Boolean {
        val timeSinceLastUpdate =
            System.currentTimeMillis() - namedScheduleEntity.lastTimeUpdate
        return timeSinceLastUpdate > SCHEDULE_UPDATE_THRESHOLD_MS && namedScheduleEntity.type != CUSTOM_SCHEDULE_TYPE
    }

    private suspend fun performNamedScheduleUpdate(
        namedScheduleEntity: NamedScheduleEntity
    ): Result<String> {
        val updatedNamedSchedule = getNewNamedSchedule(
            primaryKeyNamedSchedule = namedScheduleEntity.id,
            name = namedScheduleEntity.shortName,
            apiId = namedScheduleEntity.apiId!!,
            type = namedScheduleEntity.type
        )

        return when (updatedNamedSchedule) {
            is Result.Error -> {
                Result.Error(updatedNamedSchedule.typedError)
            }

            is Result.Success -> {
                val oldNamedSchedule = getSavedNamedScheduleById(namedScheduleEntity.id)
                oldNamedSchedule?.let {
                    mergeAndUpdateSchedules(
                        oldNamedSchedule = oldNamedSchedule,
                        newNamedSchedule = updatedNamedSchedule.data
                    )
                    Result.Success("Success update")
                } ?: Result.Error(
                    TypedError.UnexpectedError(
                        Exception("Cannot find schedule")
                    )
                )

            }
        }
    }


    private suspend fun mergeAndUpdateSchedules(
        oldNamedSchedule: NamedScheduleFormatted,
        newNamedSchedule: NamedScheduleFormatted
    ) {
        val oldSchedulesMap =
            oldNamedSchedule.schedules.associateBy { it.scheduleEntity.timetableId }

        newNamedSchedule.schedules.forEach { updatedSchedule ->
            val oldSchedule = oldSchedulesMap[updatedSchedule.scheduleEntity.timetableId]
            oldSchedule?.let {
                val updatedScheduleEntity =
                    if (oldSchedule.scheduleEntity.recurrence?.currentNumber != updatedSchedule.scheduleEntity.recurrence?.currentNumber) {
                        oldSchedule.scheduleEntity.copy(
                            recurrence = updatedSchedule.scheduleEntity.recurrence
                        )
                    } else {
                        oldSchedule.scheduleEntity
                    }

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

            } ?: insertSchedule(
                oldNamedSchedule.namedScheduleEntity.id,
                updatedSchedule
            )
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

    private suspend fun updateLastTimeUpdate(
        namedScheduleId: Long,
        lastTimeUpdate: Long
    ) {
        namedScheduleDao.updateLastTimeUpdate(
            primaryKeyNamedSchedule = namedScheduleId,
            lastTimeUpdate = lastTimeUpdate
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
                            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
                            ?.distinctBy {
                                it.customHashCode()
                            }
                            ?: emptyList()

                        val eventsByWeek = clearedEvents.groupBy {
                            it.startDatetime!!.get(WeekFields.ISO.weekBasedYear())
                        }
                        val weeksIndexes = eventsByWeek.keys.toList()
                        val checkedEvents = eventsByWeek.flatMap { (weekIndex, eventsInWeek) ->
                            eventsInWeek.map { event ->
                                event.copy(
                                    timeSlotName = getTimeSlotName(
                                        startDateTime = event.startDatetime!!,
                                        endDateTime = event.endDatetime!!
                                    ),
                                    periodNumber = (weekIndex % weeksIndexes.size + 1)
                                )
                            }
                        }
                        PeriodicContent(
                            events = checkedEvents,
                            recurrence = Recurrence(
                                frequency = "WEEKLY",
                                currentNumber = parser.parseCurrentWeek("https://www.miit.ru/people/$apiId/timetable"),
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

    private fun List<Schedule>.migrateToNewModel(
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
                    recurrence = oldSchedule.periodicContent?.let {
                        if (today > oldSchedule.timetable.startDate) {
                            val currentWeekIndex = abs(
                                ChronoUnit.WEEKS.between(
                                    oldSchedule.timetable.startDate.getFirstDayOfWeek(),
                                    LocalDate.now().getFirstDayOfWeek()
                                )
                            ).plus(1)
                            val firstWeekNumber =
                                ((currentWeekIndex + (oldSchedule.periodicContent.recurrence!!.currentNumber
                                    ?: 1))
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
                                firstWeekNumber = oldSchedule.periodicContent.recurrence.currentNumber ?: 1
                            )
                        }
                    },
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
        if (type != 1) return this
        val nameParts = this.split(" ")
        return if (nameParts.size == 3) {
            "${nameParts.first()} ${nameParts[1][0]}. ${nameParts[2][0]}."
        } else this
    }

    private fun Int.getApiTypeById() =
        when (this) {
            0 -> "group"
            1 -> "person"
            else -> "room"
        }

}