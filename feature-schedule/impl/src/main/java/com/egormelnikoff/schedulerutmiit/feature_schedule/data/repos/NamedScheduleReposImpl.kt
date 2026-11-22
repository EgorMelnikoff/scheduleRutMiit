package com.egormelnikoff.schedulerutmiit.feature_schedule.data.repos

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.NamedScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.ScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.ScheduleRemoteDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.ScheduleSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NamedScheduleReposImpl @Inject constructor(
    private val namedScheduleDataSource: NamedScheduleDataSource,
    private val scheduleDataSource: ScheduleDataSource,
    private val scheduleRemoteDataSource: ScheduleRemoteDataSource
) : NamedScheduleRepos {
    private val source = MutableStateFlow<ScheduleSource>(ScheduleSource.Default)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentNamedSchedule: Flow<NamedScheduleWithSchedules?> =
        source.flatMapLatest { source ->
            when (source) {
                is ScheduleSource.ById -> namedScheduleDataSource.observeById(source.id)

                is ScheduleSource.Default -> namedScheduleDataSource.observeDefault()

                is ScheduleSource.Temporary -> flowOf(source.schedule)
            }
        }

    override val isSaved: Boolean
        get() = source.value is ScheduleSource.Temporary


    override suspend fun get(
        namedScheduleId: Long,
        name: String,
        apiId: Int,
        type: NamedScheduleType,
    ): Pair<Boolean, NamedScheduleWithSchedules> {
        if (namedScheduleId != 0L) {
            return true to namedScheduleDataSource.getById(namedScheduleId)
        } else if (apiId != 0) {
            namedScheduleDataSource.getByApiId(apiId)?.let {
                return true to it
            }
        }

        return false to scheduleRemoteDataSource.getSchedule(namedScheduleId, name, apiId, type)
    }

    override suspend fun getAndOpen(
        namedScheduleId: Long,
        name: String,
        apiId: Int,
        type: NamedScheduleType
    ) {
        if (namedScheduleId != 0L) {
            source.value = ScheduleSource.ById(namedScheduleId)
            return
        }
        val schedule = get(namedScheduleId, name, apiId, type)

        source.value = if (schedule.first) {
            ScheduleSource.ById(schedule.second.namedSchedule.id)
        } else {
            ScheduleSource.Temporary(schedule.second)
        }
    }

    override suspend fun getById(
        namedScheduleId: Long
    ) = namedScheduleDataSource.getById(namedScheduleId)

    override suspend fun getDefault() = namedScheduleDataSource.getDefault()

    override fun observeDefault() = namedScheduleDataSource.observeDefault()

    override suspend fun openTemp(namedScheduleWithSchedules: NamedScheduleWithSchedules) {
        source.value = ScheduleSource.Temporary(namedScheduleWithSchedules)
    }

    override suspend fun openDefault() {
        source.value = ScheduleSource.Default
    }

    override suspend fun saveOpened() {
        currentNamedSchedule.first()?.let {
            val result = save(it)
            source.value = if (result.second) ScheduleSource.Default
            else ScheduleSource.ById(result.first)
        }
    }

    override suspend fun save(namedScheduleWithSchedules: NamedScheduleWithSchedules): Pair<Long, Boolean> {
        val namedScheduleId = namedScheduleDataSource.save(
            namedScheduleWithSchedules.namedSchedule
        )
        scheduleDataSource.saveAll(
            namedScheduleId, namedScheduleWithSchedules.schedulesWithEvents
        )

        if (namedScheduleDataSource.getCount() == 1) {
            namedScheduleDataSource.setDefault(namedScheduleId)
            return namedScheduleId to true
        }

        return namedScheduleId to false
    }

    override suspend fun delete(
        namedScheduleId: Long,
    ) {
        namedScheduleDataSource.deleteById(namedScheduleId)
    }

    override fun observeAll() = namedScheduleDataSource.observeAll()

    override suspend fun getAll(): List<NamedSchedule> {
        return namedScheduleDataSource.getAll()
    }

    override suspend fun setDefault(namedScheduleId: Long) {
        namedScheduleDataSource.setDefault(namedScheduleId)
    }

    override suspend fun updateName(
        namedScheduleId: Long,
        newName: String
    ) {
        namedScheduleDataSource.updateName(
            namedScheduleId = namedScheduleId,
            newName = newName
        )
    }

    override suspend fun updateLastTimeUpdate(namedScheduleId: Long) {
        namedScheduleDataSource.updateLastTimeUpdate(namedScheduleId)
    }
}