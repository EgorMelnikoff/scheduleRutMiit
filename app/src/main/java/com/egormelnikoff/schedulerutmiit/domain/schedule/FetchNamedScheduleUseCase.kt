package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.exception.ScheduleLoadException
import com.egormelnikoff.schedulerutmiit.app.extension.getShortName
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.TypedError
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.FetchNamedScheduleResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class FetchNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val scheduleNormalizer: ScheduleNormalizer,
    private val scheduleMapper: ScheduleMapper
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long = 0,
        fetchForce: Boolean = false,
        name: String,
        apiId: String,
        type: NamedScheduleType
    ): FetchNamedScheduleResult = supervisorScope {
        val savedNamedSchedule = if (!fetchForce)
            scheduleRepos.getNamedScheduleByApiId(apiId)
        else null
        savedNamedSchedule?.let {
            return@supervisorScope FetchNamedScheduleResult(
                Result.Success(savedNamedSchedule),
                true
            )
        }

        when (val timetables = scheduleRepos.fetchTimetables(apiId = apiId, type = type)) {
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

                val deferredSchedules = timetables.data.timetables.mapIndexed { index, timetable ->
                    async {
                        scheduleRepos.fetchSchedule(apiId, type, timetable.id).let { schedule ->
                            when (schedule) {
                                is Result.Success -> {
                                    val normalizedSchedule = scheduleNormalizer(
                                        schedule.data,
                                        apiId,
                                        timetable

                                    )

                                    scheduleMapper(
                                        normalizedSchedule,
                                        primaryKeyNamedSchedule,
                                        index
                                    )
                                }

                                is Result.Error ->
                                    throw ScheduleLoadException(schedule.typedError)
                            }
                        }
                    }
                }

                try {
                    val schedules = deferredSchedules.awaitAll().filterNotNull()
                    val namedScheduleEntity = NamedScheduleEntity(
                        id = primaryKeyNamedSchedule,
                        fullName = name,
                        shortName = name.getShortName(type),
                        apiId = apiId,
                        type = type,
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