package com.egormelnikoff.schedulerutmiit.tasks.domain.use_case

import com.egormelnikoff.schedulerutmiit.tasks.data.repos.TaskRepos
import javax.inject.Inject

class ObserveAllTasksUseCase @Inject constructor(
    private val taskRepos: TaskRepos
) {
    operator fun invoke() = taskRepos.observeAll()
}