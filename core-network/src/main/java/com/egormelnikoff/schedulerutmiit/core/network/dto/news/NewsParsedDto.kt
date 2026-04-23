package com.egormelnikoff.schedulerutmiit.core.network.dto.news

import androidx.annotation.Keep

@Keep
data class NewsParsedDto(
    val newsDto: NewsDto,
    val elements: MutableList<Pair<String, Any>>,
    val images: MutableList<String>
)