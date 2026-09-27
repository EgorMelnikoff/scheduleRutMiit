package com.egormelnikoff.schedulerutmiit.core.network.dto.news

data class NewsParsedDto(
    val newsDto: NewsDto,
    val paragraphs: List<Pair<String, Any>>,
    val images: List<String>
)