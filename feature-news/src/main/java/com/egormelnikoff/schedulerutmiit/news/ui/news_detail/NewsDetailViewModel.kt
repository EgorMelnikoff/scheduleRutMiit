package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.result.Result
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = NewsDetailViewModel.Factory::class
)
class NewsDetailViewModel @AssistedInject constructor(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val appRouter: AppRouter,
    @Assisted
    private val newsId: Long
) : BaseViewModel<NewsDetailState, NewsDetailAction>(NewsDetailState.Loading) {

    override fun onAction(action: NewsDetailAction) {
        when (action) {
            is NewsDetailAction.OnBackClicked -> onBackClicked()
            is NewsDetailAction.OnRetryClicked -> getNewsById(newsId)
        }
    }

    override fun onScreenInitialized() {
        super.onScreenInitialized()
        getNewsById(newsId)
    }

    private fun onBackClicked() {
        appRouter.onBack()
    }

    private fun getNewsById(id: Long) {
        viewModelScope.launch {
            updateState {
                NewsDetailState.Loading
            }

            when (val news = newsRemoteDataSource.getNewsById(id)) {
                is Result.Error -> updateState {
                    NewsDetailState.Error(news.typedError)
                }

                is Result.Success -> updateState {
                    NewsDetailState.Loaded(news.data)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(newsId: Long): NewsDetailViewModel
    }
}