package com.egormelnikoff.schedulerutmiit.data.local.dto.news

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.data.remote.dto.news.NewsDto

@Keep
data class NewsParsedDto(
    val newsDto: NewsDto,
    val elements: MutableList<Pair<String, Any>>,
    val images: MutableList<String>
)