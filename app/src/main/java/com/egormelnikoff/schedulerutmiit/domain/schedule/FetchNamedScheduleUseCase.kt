package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.exception.ScheduleLoadException
import com.egormelnikoff.schedulerutmiit.app.extension.getShortName
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.FetchNamedScheduleResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import java.time.LocalDate
import javax.inject.Inject

class FetchNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val scheduleMapper: ScheduleMapper
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long = 0,
        fetchForce: Boolean = false,
        name: String,
        apiId: Int,
        namedScheduleType: NamedScheduleType
    ): FetchNamedScheduleResult = supervisorScope {
        if (!fetchForce) {
            scheduleRepos.getNamedScheduleByApiId(apiId)?.let {
                return@supervisorScope FetchNamedScheduleResult(
                    Result.Success(it),
                    true
                )
            }
        }

        when (val timetables = scheduleRepos.fetchTimetables(
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
                            scheduleRepos.fetchScheduleParser(
                                namedScheduleType = namedScheduleType,
                                name = name,
                                apiId = apiId,
                                timetable = timetable,
                                currentGroup = if (namedScheduleType == NamedScheduleType.GROUP) {
                                    Group(
                                        id = apiId,
                                        name = name
                                    )
                                } else null
                            ).let { schedule ->
                                when (schedule) {
                                    is Result.Success -> {
                                        if (schedule.data.periodicContent?.events.isNullOrEmpty() || schedule.data.nonPeriodicContent?.events.isNullOrEmpty()) {
                                            scheduleMapper(
                                                schedule.data,
                                                primaryKeyNamedSchedule,
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
                        id = primaryKeyNamedSchedule,
                        fullName = name,
                        shortName = name.getShortName(namedScheduleType),
                        apiId = apiId.toString(),
                        type = namedScheduleType,
                        isDefault = false,
                        lastTimeUpdate = System.currentTimeMillis()
                    )
                    FetchNamedScheduleResult(
                        Result.Success(
                            NamedScheduleFormatted(
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