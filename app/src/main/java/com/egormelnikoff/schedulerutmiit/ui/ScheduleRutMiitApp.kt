package com.egormelnikoff.schedulerutmiit.ui

import android.annotation.SuppressLint
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsScreen
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsState
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsViewModel
import com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host.AppSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.widget.bottom_nav.AppBottonNavBarWidget
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_schedule.AddScheduleScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events.HiddenEventsScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.rename_schedule.RenameScheduleScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsScreen
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsScreen
import com.egormelnikoff.schedulerutmiit.search.ui.SearchScreen
import com.egormelnikoff.schedulerutmiit.tasks.ui.add_task.AddTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.edit_task.EditTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.list.TasksScreen

@Composable
fun ScheduleRutMiitApp(
    settingsViewModel: SettingsViewModel,
    settingsState: SettingsState,
    appRouter: AppRouter,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    snackbarHostState = snackbarHostState,
                    settingsViewModel = settingsViewModel,
                    settingsState = settingsState,
                    appRouter = appRouter,
                )
            },
            snackbarHostState = snackbarHostState,
            appRouter = appRouter,
        )
    }
}


@Composable
fun PageHost(
    appRouter: AppRouter,
    settingsViewModel: SettingsViewModel,
    settingsState: SettingsState,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottonNavBarWidget()
        }
    ) { padding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
            backStack = appRouter.pageBackStack,
            onBack = {
                appRouter.onBack()
            },
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            popTransitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            predictivePopTransitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            entryProvider = entryProvider {
                entry<Route.Page.Review> {
                    ReviewScreen(
                        contentPadding = padding,
                    )
                }

                entry<Route.Page.Schedule> { page ->
                    ScreenSchedule(
                        page = page,
                        snackbarHostState = snackbarHostState,
                        contentPadding = padding,
                    )
                }

                entry<Route.Page.Tasks> {
                    TasksScreen(
                        contentPadding = padding,
                    )
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        state = settingsState,
                        onAction = { action ->
                            settingsViewModel.onAction(action)
                        },
                        contentPadding = padding
                    )
                }
            }
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RootHost(
    pageHost: @Composable () -> Unit,
    appRouter: AppRouter,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = {
            if (appRouter.dialogBackStack.size > 1) {
                AppSnackbarHost(
                    snackBarHostState = snackbarHostState
                )
            }
        }
    ) {
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                ),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            backStack = appRouter.dialogBackStack,
            onBack = {
                appRouter.onBack()
            },
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            popTransitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            predictivePopTransitionSpec = {
                when (it) {
                    0 -> {
                        fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                    }

                    1 -> {
                        fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                    }

                    else -> {
                        fadeIn() togetherWith fadeOut()
                    }
                }
            },
            entryProvider = entryProvider {
                entry<Route.Dialog.Empty> {
                    pageHost.invoke()
                }

                entry<Route.Dialog.EventDialog> { dialog ->
                    EventScreen(
                        eventDialog = dialog,
                    )
                }

                entry<Route.Dialog.NewsList> {
                    NewsScreen()
                }

                entry<Route.Dialog.NewsDialog> { dialog ->
                    NewsScreen(
                        newsDialog = dialog
                    )
                }

                entry<Route.Dialog.AddTaskDialog> {
                    AddTaskDialog()
                }

                entry<Route.Dialog.EditTaskDialog> { dialog ->
                    EditTaskDialog(
                        editTaskDialog = dialog
                    )
                }

                entry<Route.Dialog.EditEventDialog> { editEventDialog ->
                    AddEventScreen(
                        editEventDialog = editEventDialog
                    )
                }

                entry<Route.Dialog.SearchDialog> {
                    SearchScreen()
                }
                entry<Route.Dialog.CurriculumDialog> {
                    CurriculumScreen()
                }
                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleScreen()
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { renameDialog ->
                    RenameScheduleScreen(
                        renameDialog
                    )
                }
                entry<Route.Dialog.HiddenEventsDialog> { hiddenEventsDialog ->
                    HiddenEventsScreen(
                        hiddenEventsDialog
                    )
                }
            }
        )
    }
}