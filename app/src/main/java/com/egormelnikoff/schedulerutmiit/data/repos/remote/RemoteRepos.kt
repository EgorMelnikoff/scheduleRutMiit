package com.egormelnikoff.schedulerutmiit.data.repos.remote

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.Api
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiRoutes.GROUPS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiRoutes.GROUP_SCHEDULE
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiRoutes.NEWS
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiRoutes.NEWS_CATALOG
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiRoutes.PERSON_SCHEDULE
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiRoutes.ROOM_SCHEDULE
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.PEOPLE
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.model.Institutes
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.model.NewsList
import com.egormelnikoff.schedulerutmiit.model.NonPeriodicContent
import com.egormelnikoff.schedulerutmiit.model.PeriodicContent
import com.egormelnikoff.schedulerutmiit.model.Person
import com.egormelnikoff.schedulerutmiit.model.Schedule
import com.egormelnikoff.schedulerutmiit.model.TelegramPage
import com.egormelnikoff.schedulerutmiit.model.Timetable
import com.egormelnikoff.schedulerutmiit.model.TimetableType
import com.egormelnikoff.schedulerutmiit.model.Timetables
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateFirstDayOfWeek
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import kotlin.math.abs

interface RemoteRepos {
    suspend fun getInstitutes(): Result<Institutes>
    suspend fun getPeople(query: String): Result<List<Person>>
    suspend fun getNamedSchedule(
        namedScheduleId: Long = 0,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted>
    suspend fun getNewsList(page: String): Result<NewsList>
    suspend fun getNewsById(id: Long): Result<News>
    suspend fun getTgChannelInfo(url: String): Result<TelegramPage>
}

class RemoteReposImpl(
    private val api: Api,
    private val parser: Parser
) : RemoteRepos {
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
                        val scheduleJson = when (type) {
                            0 -> api.getData(URL("$GROUP_SCHEDULE$apiId/${timetable.id}"))
                            1 -> api.getData(URL("$PERSON_SCHEDULE$apiId/${timetable.id}"))
                            2 -> api.getData(URL("$ROOM_SCHEDULE$apiId/${timetable.id}"))
                            else -> Result.Success(null)
                        }
                        when (val schedule = api.parseJson(scheduleJson, Schedule::class.java)) {
                            is Result.Error -> continue
                            is Result.Success -> {
                                val fixedSchedule = fixApiIssuance(timetable, schedule.data)
                                schedules.add(fixedSchedule!!)
                            }
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

    override suspend fun getPeople(query: String): Result<List<Person>> {
        return try {
            parser.parsePeople("$PEOPLE$query")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getInstitutes(): Result<Institutes> {
        return try {
            val institutesJson = api.getData(URL(GROUPS))
            api.parseJson(institutesJson, Institutes::class.java)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNewsList(page: String): Result<NewsList> {
        return try {
            val newsListJson = api.getData(URL("${NEWS_CATALOG}&from=$page&to=$page"))
            api.parseJson(newsListJson, NewsList::class.java)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNewsById(id: Long): Result<News> {
        return try {
            val newsJson = api.getData(URL("$NEWS$id"))
            api.parseJson(newsJson, News::class.java)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTgChannelInfo(url: String): Result<TelegramPage> {
        return parser.parseChannelInfo(url)
    }

    private suspend fun getTimetables(
        apiId: String,
        type: Int
    ): Result<Timetables> {
        val timetablesJson = when (type) {
            0 -> api.getData(URL("$GROUP_SCHEDULE$apiId"))
            1 -> api.getData(URL("$PERSON_SCHEDULE$apiId"))
            2 -> api.getData(URL("$ROOM_SCHEDULE$apiId"))
            else -> Result.Success(null)
        }
        return when (val timetables = api.parseJson(timetablesJson, Timetables::class.java)) {
            is Result.Error -> Result.Error(timetables.exception)
            is Result.Success -> timetables
        }

    }

    private fun fixApiIssuance(timetable: Timetable, schedule: Schedule): Schedule? {
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
                                currentNumber = 1, //parse
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
                val scheduleEntity = ScheduleEntity(
                    namedScheduleId = id,
                    startDate = oldSchedule.timetable?.startDate!!,
                    endDate = oldSchedule.timetable.endDate!!,
                    recurrence = if (oldSchedule.periodicContent != null) {
                        if (LocalDate.now() > oldSchedule.timetable.startDate) {
                            val currentWeekIndex = abs(
                                ChronoUnit.WEEKS.between(
                                    calculateFirstDayOfWeek(oldSchedule.timetable.startDate),
                                    calculateFirstDayOfWeek(LocalDate.now())
                                ) + oldSchedule.periodicContent.recurrence!!.currentNumber!!
                            ) - 1
                            val firstWeekNumber =
                                (currentWeekIndex % oldSchedule.periodicContent.recurrence.interval!!).toInt()
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