package com.egormelnikoff.schedulerutmiit.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.classes.News
import com.egormelnikoff.schedulerutmiit.classes.NewsShort
import com.egormelnikoff.schedulerutmiit.data.repos.Result
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteRepos
import com.egormelnikoff.schedulerutmiit.data.repos.remote.parser.Parser
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


class NewsViewModel : ViewModel() {
    private val _stateNewsList = MutableStateFlow<NewsListState>(NewsListState.Loading)
    val stateNewsList: StateFlow<NewsListState> = _stateNewsList

    private val _stateNews = MutableStateFlow<NewsState>(NewsState.Loading)
    val stateNews: StateFlow<NewsState> = _stateNews

    fun getNewsList(page: Int) {
        viewModelScope.launch {
            _stateNewsList.value = NewsListState.Loading
            when (val newsList = RemoteRepos.getNewsList(page = page.toString())) {
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
    }

    fun getNewsById(id: Long) {
        viewModelScope.launch {
            _stateNews.value = NewsState.Loading
            when (val news = RemoteRepos.getNewsById(id)) {
                is Result.Error -> _stateNews.value = NewsState.Error
                is Result.Success -> {
                    _stateNews.value = NewsState.Loaded(
                        news = Parser.parseNews(news.data)
                    )
                }
            }
        }
    }
}
