package com.egormelnikoff.schedulerutmiit.news.ui.news_list

internal sealed interface NewsListAction {
    data class OnNewsClick(val newsId: Long) : NewsListAction
}