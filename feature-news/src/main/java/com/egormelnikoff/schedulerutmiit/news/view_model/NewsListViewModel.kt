package com.egormelnikoff.schedulerutmiit.news.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.news.domain.use_case.GetNewsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    getNewsListUseCase: GetNewsListUseCase
) : ViewModel() {
    val newsListFlow: Flow<PagingData<NewsShortDto>> =
        getNewsListUseCase().cachedIn(viewModelScope)
}