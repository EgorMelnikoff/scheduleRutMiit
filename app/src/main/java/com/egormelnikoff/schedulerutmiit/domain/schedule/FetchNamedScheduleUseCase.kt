package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.exception.ScheduleLoadException
import com.egormelnikoff.schedulerutmiit.app.extension.getShortName
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.datasource.remote.schedule.ScheduleRemoteDataSource
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.FetchNamedScheduleResult
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

class FetchNamedScheduleUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val scheduleRemoteDataSource: ScheduleRemoteDataSource,
    private val scheduleMapper: ScheduleMapper
) {
    suspend operator fun invoke(
        namedScheduleId: Long = 0,
        fetchForce: Boolean = false,
        name: String,
        apiId: Int,
        namedScheduleType: NamedScheduleType
    ): FetchNamedScheduleResult = supervisorScope {
        if (!fetchForce) {
            namedScheduleRepos.getByApiId(apiId)?.let {
                return@supervisorScope FetchNamedScheduleResult(
                    Result.Success(it),
                    true
                )
            }
        }

        when (val timetables = scheduleRemoteDataSource.fetchTimetables(
            apiId = apiId,
            type = namedScheduleType
        )) {
            is Result.Error -> {
                return@supervisorScope FetchNamedScheduleResult(
                    Result.Error(timetables.typedError),
                    false
                )
            }

            is Result.Success -> {
                if (timetables.data.timetables.isEmpty()) {
                    return@supervisorScope FetchNamedScheduleResult(
                        Result.Error(
                            TypedError.EmptyBodyError
                        ), false
                    )
                }

                val deferredSchedules = timetables.data.timetables
                    .filter { it.endDate >= LocalDate.now() }
                    .mapIndexed { index, timetable ->
                        async {
                            scheduleRemoteDataSource.fetchSchedule(
                                namedScheduleType = namedScheduleType,
                                name = name,
                                apiId = apiId,
                                timetable = timetable,
                                currentGroup = if (namedScheduleType == NamedScheduleType.GROUP) {
                                    GroupDto(
                                        id = apiId,
                                        name = name
                                    )
                                } else null
                            ).let { schedule ->
                                when (schedule) {
                                    is Result.Success -> {
                                        if (schedule.data.periodic?.events.isNullOrEmpty() || schedule.data.nonPeriodic?.events.isNullOrEmpty()) {
                                            scheduleMapper(
                                                schedule.data,
                                                namedScheduleId,
                                                index
                                            )
                                        } else {
                                            throw ScheduleLoadException(TypedError.EmptyBodyError)
                                        }
                                    }

                                    is Result.Error ->
                                        throw ScheduleLoadException(schedule.typedError)
                                }
                            }
                        }
                    }

                try {
                    val schedules = deferredSchedules.awaitAll()
                    val namedScheduleEntity = NamedScheduleEntity(
                        id = namedScheduleId,
                        fullName = name,
                        shortName = name.getShortName(namedScheduleType),
                        apiId = apiId.toString(),
                        type = namedScheduleType,
                        isDefault = false,
                        lastTimeUpdate = System.currentTimeMillis()
                    )
                    FetchNamedScheduleResult(
                        Result.Success(
                            NamedSchedule(
                                namedScheduleEntity = namedScheduleEntity,
                                schedules = schedules
                            )
                        ), false
                    )
                } catch (e: ScheduleLoadException) {
                    FetchNamedScheduleResult(
                        Result.Error(e.error),
                        false
                    )
                }
            }
        }
    }
}