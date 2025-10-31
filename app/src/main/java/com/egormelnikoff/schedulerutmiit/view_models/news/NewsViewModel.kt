package com.egormelnikoff.schedulerutmiit.view_models.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsShort
import com.egormelnikoff.schedulerutmiit.data.Error
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.local.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsRepos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NewsViewModel {
    val uiState: StateFlow<NewsState>
    val newsListFlow: Flow<PagingData<NewsShort>>
    fun getNewsById(id: Long)
}

data class NewsState(
    val currentNews: News? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewsViewModelImpl @Inject constructor(
    private val newsRepos: NewsRepos,
    private val resourcesManager: ResourcesManager
) : ViewModel(), NewsViewModel {

    private val _uiState = MutableStateFlow(NewsState())
    override val uiState = _uiState.asStateFlow()

    private var newsJob: Job? = null

    override val newsListFlow: Flow<PagingData<NewsShort>> = newsRepos
        .getNewsListFlow()
        .cachedIn(viewModelScope)

    override fun getNewsById(id: Long) {
        _uiState.update { it.copy(isLoading = true) }
        val newNewsJob = viewModelScope.launch {
            newsJob?.cancelAndJoin()
            when (val news = newsRepos.getNewsById(id)) {
                is Result.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = Error.getErrorMessage(
                            resourcesManager = resourcesManager,
                            data = news.error
                        )
                    )
                }

                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            currentNews = newsRepos.parseNews(news.data)
                        )
                    }
                }
            }
        }
        newsJob = newNewsJob
    }
}