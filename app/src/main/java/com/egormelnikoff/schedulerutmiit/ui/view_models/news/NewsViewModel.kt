package com.egormelnikoff.schedulerutmiit.ui.view_models.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.repos.news.NewsRepos
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.model.NewsShort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class NewsViewModelImpl @Inject constructor(
    private val newsRepos: NewsRepos
) : ViewModel(), NewsViewModel {

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
            val newsList = newsRepos.getNewsList(page = page.toString())
            println(newsList)
            when (newsList) {
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
            when (val news = newsRepos.getNewsById(id)) {
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
                            currentNews = newsRepos.parseNews(news.data)
                        )
                    }
                }
            }
        }
        newsJob = newNewsJob
    }
}