package com.egormelnikoff.schedulerutmiit.search.domain.use_case

import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchQueryRepos
import javax.inject.Inject

class ObserveSearchHistoryUseCase @Inject constructor(
    private val searchQueryRepos: SearchQueryRepos
) {
    operator fun invoke() = searchQueryRepos.observeAll()
}