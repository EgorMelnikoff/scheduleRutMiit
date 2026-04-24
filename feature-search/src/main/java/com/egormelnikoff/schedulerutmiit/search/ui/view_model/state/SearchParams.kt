package com.egormelnikoff.schedulerutmiit.search.ui.view_model.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType

@Keep
data class SearchParams(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL
)