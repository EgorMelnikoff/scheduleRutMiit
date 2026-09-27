package com.egormelnikoff.schedulerutmiit.news.ui.news_list

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.news.domain.use_case.GetNewsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    getNewsListUseCase: GetNewsListUseCase,
    private val appRouter: AppRouter
) : BaseViewModel<Unit, NewsListAction>(Unit) {

    val newsListFlow: Flow<PagingData<NewsShortDto>> =
        getNewsListUseCase().cachedIn(viewModelScope)

    override fun onAction(action: NewsListAction) {
        when (action) {
            is NewsListAction.OnNewsClick -> appRouter.openDialog(Route.Dialog.NewsDialog(action.newsId))
        }
    }
}