package com.egormelnikoff.schedulerutmiit.ui.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.egormelnikoff.schedulerutmiit.AppContainerInterface
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalReposInterface
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteReposInterface
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.model.NewsShort
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NewsListState {
    data object Loading : NewsListState
    data class Loaded(
        var news: List<NewsShort>
    ) : NewsListState

    data object Error : NewsListState
}

sealed interface NewsState {
    data object Loading : NewsState
    data class Loaded(
        val news: News
    ) : NewsState

    data object Error : NewsState
}

class NewsViewModel(
    private val localRepos: LocalReposInterface,
    private val remoteRepos: RemoteReposInterface

) : ViewModel() {
    companion object {
        fun provideFactory(container: AppContainerInterface): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    return NewsViewModel(
                        localRepos = container.localRepos,
                        remoteRepos = container.remoteRepos
                    ) as T
                }
            }
        }
    }

    private var newsJob: Job? = null

    private val _stateNewsList = MutableStateFlow<NewsListState>(NewsListState.Loading)
    val stateNewsList: StateFlow<NewsListState> = _stateNewsList

    private val _stateNews = MutableStateFlow<NewsState>(NewsState.Loading)
    val stateNews: StateFlow<NewsState> = _stateNews

    fun getNewsList(page: Int) {
        _stateNewsList.value = NewsListState.Loading
        val newNewsListJob = viewModelScope.launch {
            newsJob?.cancelAndJoin()
            when (val newsList = remoteRepos.getNewsList(page = page.toString())) {
                is Result.Error -> _stateNewsList.value = NewsListState.Error
                is Result.Success -> {
                    val updatedItems = newsList.data.items.map { newsShort ->
                        newsShort.apply { thumbnail = "https://www.miit.ru$thumbnail" }
                    }
                    _stateNewsList.value = NewsListState.Loaded(
                        news = updatedItems.filter { it.secondary.text != "Наши защиты" }
                    )
                }
            }
        }
        newsJob = newNewsListJob
    }

    fun getNewsById(id: Long) {
        _stateNews.value = NewsState.Loading
        val newNewsJob = viewModelScope.launch {
            newsJob?.cancelAndJoin()
            when (val news = remoteRepos.getNewsById(id)) {
                is Result.Error -> _stateNews.value = NewsState.Error
                is Result.Success -> {
                    _stateNews.value = NewsState.Loaded(
                        news = localRepos.parseNews(news.data)
                    )
                }
            }
        }
        newsJob = newNewsJob
    }
}