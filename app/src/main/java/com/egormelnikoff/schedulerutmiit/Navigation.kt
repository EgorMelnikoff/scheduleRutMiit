package com.egormelnikoff.schedulerutmiit

import androidx.compose.ui.graphics.vector.ImageVector

data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

sealed class Routes(val route: String) {
    data object Search : Routes("search")
    data object Schedule : Routes("schedule")
    data object News : Routes("news")
    data object Settings : Routes("settings")
}