package com.egormelnikoff.schedulerutmiit.view_models.search

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.SearchType

@Keep
data class SearchParams(
    val query: String = "",
    val searchType: SearchType = SearchType.ALL
)