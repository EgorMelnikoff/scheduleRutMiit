package com.egormelnikoff.schedulerutmiit.data.repos

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalRepos
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteRepos
import com.egormelnikoff.schedulerutmiit.model.Institutes
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.model.NewsList
import com.egormelnikoff.schedulerutmiit.model.Person
import com.egormelnikoff.schedulerutmiit.model.TelegramPage
import java.time.LocalDate
import java.util.concurrent.TimeUnit

interface Repos {
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
        id: Long
    )
    suspend fun isSavingAvailable(): Boolean
    suspend fun getAllNamedSchedules(): List<NamedScheduleEntity>
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

    suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity
    ): Result<String>
}

class ReposImpl(
    private val localRepos: LocalRepos,
    private val remoteRepos: RemoteRepos
) : Repos {
    companion object {
        val SCHEDULE_UPDATE_THRESHOLD_MS = TimeUnit.HOURS.toMillis(6)
        const val CUSTOM_SCHEDULE_TYPE = 3
    }

    override suspend fun insertNamedSchedule(namedSchedule: NamedScheduleFormatted) {
        localRepos.insertNamedSchedule(namedSchedule)
    }

    override suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted
    ) {
        localRepos.insertSchedule(namedScheduleId, scheduleFormatted)
    }

    override suspend fun deleteNamedSchedule(primaryKey: Long, isDefault: Boolean) {
        localRepos.deleteNamedSchedule(primaryKey, isDefault)
    }

    override suspend fun deleteSchedule(id: Long) {
        localRepos.deleteSchedule(id)
    }

    override suspend fun isSavingAvailable(): Boolean {
        return localRepos.isSavingAvailable()
    }

    override suspend fun getAllNamedSchedules(): List<NamedScheduleEntity> {
        return localRepos.getAllNamedSchedules()
    }

    override suspend fun getNamedScheduleByApiId(apiId: String): NamedScheduleFormatted? {
        return localRepos.getNamedScheduleByApiId(apiId)
    }

    override suspend fun getNamedScheduleById(idNamedSchedule: Long): NamedScheduleFormatted? {
        return localRepos.getNamedScheduleById(idNamedSchedule)
    }

    override suspend fun updatePriorityNamedSchedule(id: Long) {
        localRepos.updatePriorityNamedSchedule(id)
    }

    override suspend fun updatePrioritySchedule(idSchedule: Long, idNamedSchedule: Long) {
        localRepos.updatePriorityNamedSchedule(idSchedule)
    }

    override suspend fun updateEventExtra(
        scheduleId: Long,
        event: Event,
        tag: Int,
        comment: String
    ) {
        localRepos.updateEventExtra(scheduleId, event, tag, comment)
    }

    override fun parseNews(news: News): News {
        return localRepos.parseNews(news)
    }

    override suspend fun getInstitutes(): Result<Institutes> {
        return remoteRepos.getInstitutes()
    }

    override suspend fun getPeople(query: String): Result<List<Person>> {
        return remoteRepos.getPeople(query)
    }

    override suspend fun getNamedSchedule(
        namedScheduleId: Long,
        name: String,
        apiId: String,
        type: Int
    ): Result<NamedScheduleFormatted> {
        return remoteRepos.getNamedSchedule(namedScheduleId, name, apiId, type)
    }

    override suspend fun getNewsList(page: String): Result<NewsList> {
        return remoteRepos.getNewsList(page)
    }

    override suspend fun getNewsById(id: Long): Result<News> {
        return remoteRepos.getNewsById(id)
    }

    override suspend fun getTgChannelInfo(url: String): Result<TelegramPage> {
        return remoteRepos.getTgChannelInfo(url)
    }

    override suspend fun updateNamedSchedule(
        namedScheduleEntity: NamedScheduleEntity
    ): Result<String> {
        if (shouldUpdateNamedSchedule(namedScheduleEntity)) {
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
        val remoteResult = remoteRepos.getNamedSchedule(
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
                val oldNamedSchedule = localRepos.getNamedScheduleById(namedScheduleEntity.id)
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
                    val oldEvent = oldSchedule.events.find { it == event }
                    event.copy(
                        id = oldEvent?.id ?: 0L
                    )
                }

                val updatedScheduleWithId = ScheduleFormatted(
                    scheduleEntity = updatedScheduleEntity,
                    events = updatedEvents,
                    eventsExtraData = oldSchedule.eventsExtraData
                )
                localRepos.deleteSchedule(oldSchedule.scheduleEntity.id)
                localRepos.insertSchedule(
                    oldNamedSchedule.namedScheduleEntity.id,
                    updatedScheduleWithId
                )

            } else {
                localRepos.insertSchedule(oldNamedSchedule.namedScheduleEntity.id, updatedSchedule)
            }
        }

        oldNamedSchedule.schedules.forEach { oldSchedule ->
            val stillExists =
                newNamedSchedule.schedules.any { it.scheduleEntity.timetableId == oldSchedule.scheduleEntity.timetableId }
            val isOutdated = LocalDate.now() > oldSchedule.scheduleEntity.endDate
            if (!stillExists && isOutdated) {
                localRepos.deleteSchedule(
                    id = oldSchedule.scheduleEntity.id
                )
            }
        }
        localRepos.updateLastTimeUpdate(
            oldNamedSchedule.namedScheduleEntity.id,
            System.currentTimeMillis()
        )
    }
}