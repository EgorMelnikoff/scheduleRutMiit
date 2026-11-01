package com.egormelnikoff.schedulerutmiit.feature_curriculum.data.repos

import com.egormelnikoff.schedulerutmiit.feature_curriculum.data.remote.CurriculumRemoteDataSource
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.model.Subject
import com.egormelnikoff.schedulerutmiit.feature_curriculum.domain.repos.CurriculumRepos
import javax.inject.Inject

internal class CurriculumReposImpl @Inject constructor(
    private val curriculumRemoteDataSource: CurriculumRemoteDataSource
) : CurriculumRepos {
    override suspend fun fetchSubjects(id: String, page: Int): List<Subject> =
        curriculumRemoteDataSource.fetchSubjects(id, page)
}