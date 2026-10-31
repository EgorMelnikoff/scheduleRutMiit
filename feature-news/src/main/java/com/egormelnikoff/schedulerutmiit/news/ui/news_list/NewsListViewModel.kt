package com.egormelnikoff.schedulerutmiit.news.ui.news_list

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsShort
import com.egormelnikoff.schedulerutmiit.news.domain.use_case.GetNewsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class NewsListViewModel @Inject constructor(
    getNewsListUseCase: GetNewsListUseCase,
    private val appRouter: AppRouter
) : BaseViewModel<Unit, NewsListAction>(Unit) {

    val newsListFlow: Flow<PagingData<NewsShort>> =
        getNewsListUseCase().cachedIn(viewModelScope)

    override fun onAction(action: NewsListAction) {
        when (action) {
            is NewsListAction.OnNewsClick -> appRouter.openDialog(Route.Dialog.NewsDialog(action.newsId))
        }
    }
}