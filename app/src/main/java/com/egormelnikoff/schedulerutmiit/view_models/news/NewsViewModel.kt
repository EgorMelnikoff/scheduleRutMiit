package com.egormelnikoff.schedulerutmiit.view_models.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egormelnikoff.schedulerutmiit.app.dto.remote.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.app.network.result.Result
import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError
import com.egormelnikoff.schedulerutmiit.app.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.datasource.remote.news.NewsRemoteDataSource
import com.egormelnikoff.schedulerutmiit.domain.news.GetNewsListUseCase
import com.egormelnikoff.schedulerutmiit.view_models.news.state.NewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    getNewsListUseCase: GetNewsListUseCase,
    private val resourcesManager: ResourcesManager
) : ViewModel() {
    private val _newsState = MutableStateFlow(NewsState())
    val newsState = _newsState.asStateFlow()

    private var newsJob: Job? = null

    val newsListFlow: Flow<PagingData<NewsShortDto>> = getNewsListUseCase()
        .cachedIn(viewModelScope)

    fun getNewsById(id: Long) {
        _newsState.update { it.copy(isLoading = true) }
        val newNewsJob = viewModelScope.launch {
            newsJob?.cancelAndJoin()
            when (val news = newsRemoteDataSource.getNewsById(id)) {
                is Result.Error -> _newsState.update {
                    it.copy(
                        isLoading = false,
                        error = TypedError.getErrorMessage(
                            resourcesManager = resourcesManager,
                            typedError = news.typedError
                        )
                    )
                }

                is Result.Success -> {
                    _newsState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            currentNews = news.data
                        )
                    }
                }
            }
        }
        newsJob = newNewsJob
    }

    fun setDefaultNewsState() {
        _newsState.update {
            it.copy(
                isLoading = false,
                error = null,
                currentNews = null
            )
        }
    }
}