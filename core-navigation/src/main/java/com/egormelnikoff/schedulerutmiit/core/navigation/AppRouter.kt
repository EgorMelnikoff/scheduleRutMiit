package com.egormelnikoff.schedulerutmiit.core.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route

interface AppRouter {
    val pageBackStack: SnapshotStateList<Route.Page>
    val dialogBackStack: SnapshotStateList<Route.Dialog>

    fun openPage(page: Route.Page)
    fun navigateToStartRage()
    fun openDialog(dialog: Route.Dialog)
    fun onBack()
    fun lastPage(): Route.Page
}