package com.egormelnikoff.schedulerutmiit.ui.view_models.search.state

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType

@Keep
data class SearchParams(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL
)