package com.egormelnikoff.schedulerutmiit.search.ui.view_model.state

import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType

data class SearchParams(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL
)