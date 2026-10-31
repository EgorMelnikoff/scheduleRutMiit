package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

internal sealed interface NewsDetailAction {
    data object OnBackClicked : NewsDetailAction
    data object OnRetryClicked : NewsDetailAction
}