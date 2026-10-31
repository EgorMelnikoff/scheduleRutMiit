package com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos

import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model.Subject

internal interface CurriculumRepos {
    suspend fun fetchSubjects(id: String, page: Int): List<Subject>
}