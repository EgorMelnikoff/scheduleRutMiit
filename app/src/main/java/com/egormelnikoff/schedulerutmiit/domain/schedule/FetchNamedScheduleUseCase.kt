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
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class FetchNamedScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val scheduleNormalizer: ScheduleNormalizer,
    private val scheduleMapper: ScheduleMapper
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long = 0,
        name: String,
        apiId: String,
        type: NamedScheduleType
    ): FetchNamedScheduleResult = coroutineScope {
        val savedNamedSchedule = scheduleRepos.getNamedScheduleByApiId(apiId)
        savedNamedSchedule?.let {
            return@coroutineScope FetchNamedScheduleResult(
                Result.Success(savedNamedSchedule),
                true
            )
        }

        when (val timetables = scheduleRepos.fetchTimetables(apiId = apiId, type = type)) {
            is Result.Error -> {
                return@coroutineScope FetchNamedScheduleResult(
                    Result.Error(timetables.typedError),
                    false
                )
            }

            is Result.Success -> {
                if (timetables.data.timetables.isEmpty()) {
                    return@coroutineScope FetchNamedScheduleResult(
                        Result.Error(
                            TypedError.EmptyBodyError
                        ), false
                    )
                }

                val deferredSchedules = timetables.data.timetables.mapIndexed { index, timetable ->
                    async {
                        scheduleRepos.fetchSchedule(apiId, type, timetable.id)
                            .let { result ->
                                when (result) {
                                    is Result.Success -> {
                                        val normalizedSchedule = scheduleNormalizer.invoke(
                                            result.data,
                                            apiId,
                                            timetable

                                        )

                                        scheduleMapper.invoke(
                                            normalizedSchedule,
                                            primaryKeyNamedSchedule,
                                            index
                                        )
                                    }

                                    is Result.Error ->
                                        throw ScheduleLoadException(result.typedError)
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