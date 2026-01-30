package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.enums.SearchType

@Keep
data class SearchParams(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL
)