package com.egormelnikoff.schedulerutmiit.news.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.core.common.resources.getErrorMessage
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRemoteDataSource
import com.egormelnikoff.schedulerutmiit.news.view_model.state.NewsState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = NewsViewModel.Factory::class
)
class NewsViewModel @AssistedInject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val resourcesManager: ResourcesManager,
    @Assisted
    private val newsId: Long
) : ViewModel() {
    private val _newsState = MutableStateFlow(NewsState())
    val newsState = _newsState.asStateFlow()

    init {
        getNewsById(newsId)
    }

    fun getNewsById(id: Long) {
        viewModelScope.launch {
            _newsState.update { it.copy(isLoading = true) }

            when (val news = newsRemoteDataSource.getNewsById(id)) {
                is Result.Error -> _newsState.update {
                    it.copy(
                        isLoading = false,
                        error = getErrorMessage(
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
    }

    @AssistedFactory
    interface Factory {
        fun create(newsId: Long): NewsViewModel
    }
}