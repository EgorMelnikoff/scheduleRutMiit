package com.egormelnikoff.schedulerutmiit.app.dto.local.news

import androidx.annotation.Keep
import com.egormelnikoff.schedulerutmiit.app.dto.remote.news.NewsDto

@Keep
data class NewsParsedDto(
    val newsDto: NewsDto,
    val elements: MutableList<Pair<String, Any>>,
    val images: MutableList<String>
)