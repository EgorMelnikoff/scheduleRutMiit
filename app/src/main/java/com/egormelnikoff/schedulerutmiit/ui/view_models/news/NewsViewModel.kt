package com.egormelnikoff.schedulerutmiit.ui.view_models.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainer
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.repos.Repos
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.model.NewsShort
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface NewsViewModel {
    val uiState: StateFlow<NewsState>
    fun getNewsList(page: Int)
    fun getNewsById(id: Long)
}

data class NewsState(
    val newsList: List<NewsShort> = listOf(),
    val currentNews: News? = null,
    val isError: Boolean = false,
    val isLoading: Boolean = false
)

class NewsViewModelImpl(
    private val repos: Repos

) : ViewModel(), NewsViewModel {
    companion object {
        fun provideFactory(container: AppContainer): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    return NewsViewModelImpl(
                        repos = container.repos
                    ) as T
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(NewsState())
    override val uiState = _uiState.asStateFlow()

    private var newsJob: Job? = null

    init {
        getNewsList(1)
    }

    override fun getNewsList(page: Int) {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        val newNewsListJob = viewModelScope.launch {
            newsJob?.cancelAndJoin()
            when (val newsList = repos.getNewsList(page = page.toString())) {
                is Result.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true
                    )
                }

                is Result.Success -> {
                    val updatedItems = newsList.data.items.map { newsShort ->
                        newsShort.apply { thumbnail = "https://www.miit.ru$thumbnail" }
                    }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isError = false,
                            newsList = updatedItems.filter { it.secondary.text != "Наши защиты" }
                        )
                    }
                }
            }
        }
        newsJob = newNewsListJob
    }

    override fun getNewsById(id: Long) {
        _uiState.update { it.copy(isLoading = true) }
        val newNewsJob = viewModelScope.launch {
            newsJob?.cancelAndJoin()
            when (val news = repos.getNewsById(id)) {
                is Result.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true
                    )
                }

                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            currentNews = repos.parseNews(news.data)
                        )
                    }
                }
            }
        }
        newsJob = newNewsJob
    }
}