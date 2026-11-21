package com.egormelnikoff.schedulerutmiit.tasks.domain.use_case

import com.egormelnikoff.schedulerutmiit.tasks.domain.repos.TaskRepos
import javax.inject.Inject

internal class ObserveAllTasksUseCase @Inject constructor(
    private val taskRepos: TaskRepos
) {
    operator fun invoke() = taskRepos.observeAll()
}