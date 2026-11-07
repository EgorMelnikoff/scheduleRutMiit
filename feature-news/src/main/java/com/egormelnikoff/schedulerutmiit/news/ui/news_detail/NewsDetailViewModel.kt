package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.network.handler.errorHandler
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.news.domain.repos.NewsRepos
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(
    assistedFactory = NewsDetailViewModel.Factory::class
)
internal class NewsDetailViewModel @AssistedInject constructor(
    private val newsRemoteDataSource: NewsRepos,
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
        launchToCoroutine(
            handler = errorHandler(
                tag = "News $id"
            ) { throwable ->
                updateState {
                    NewsDetailState.Error(throwable.toAppError())
                }
            }
        ) {
            val news = newsRemoteDataSource.getNewsById(id)

            updateState {
                NewsDetailState.Loaded(news)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(newsId: Long): NewsDetailViewModel
    }
}