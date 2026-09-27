package com.egormelnikoff.schedulerutmiit.news.ui.news_detail

sealed interface NewsDetailAction {
    data object OnBackClicked : NewsDetailAction
    data object OnRetryClicked : NewsDetailAction
}