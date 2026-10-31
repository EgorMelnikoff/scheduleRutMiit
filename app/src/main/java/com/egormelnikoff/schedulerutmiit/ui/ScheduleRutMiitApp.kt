package com.egormelnikoff.schedulerutmiit.ui

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.model.AppSettings
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBar
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.BarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host.AppSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumScreen
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsScreen
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.add_event.AddEventScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.add_schedule.AddScheduleScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.event.EventScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.hidden_events.HiddenEventsScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.rename_schedule.RenameScheduleScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.search.ui.SearchScreen
import com.egormelnikoff.schedulerutmiit.tasks.ui.add_task.AddTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.edit_task.EditTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.list.TasksScreen
import com.egormelnikoff.schedulerutmiit.ui.settings.PreferencesViewModel
import com.egormelnikoff.schedulerutmiit.ui.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainAction
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel
import java.time.LocalDate

@Composable
fun ScheduleRutMiitApp(
    preferencesViewModel: PreferencesViewModel,
    appRouter: AppRouter,
    appSettings: AppSettings,
) {
    val mainViewModel = hiltViewModel<MainViewModel>()

    val hourlyDateTime by mainViewModel.hourlyDateTime.collectAsStateWithLifecycle()


    val snackbarHostState = remember { SnackbarHostState() }


    UiEventProcessor(
        mainViewModel.uiEvent,
        snackbarHostState
    )


    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    mainViewModel = mainViewModel,
                    preferencesViewModel = preferencesViewModel,


                    appRouter = appRouter,
                    snackbarHostState = snackbarHostState,

                    appSettings = appSettings
                ) { date ->
                    hourlyDateTime.toLocalDate() == date
                }
            },
            snackbarHostState = snackbarHostState,
            appRouter = appRouter,
        )
    }
}


@Composable
fun PageHost(
    mainViewModel: MainViewModel,
    preferencesViewModel: PreferencesViewModel,

    appRouter: AppRouter,
    snackbarHostState: SnackbarHostState,

    appSettings: AppSettings,
    isToday: (LocalDate) -> Boolean
) {
    val appState by mainViewModel.state.collectAsStateWithLifecycle()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        mainViewModel.onAction(
            MainAction.ImportData(
                uri,
            )
        )
    }

    val currentPage = appRouter.lastPage()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            if (appRouter.dialogBackStack.size <= 1) {
                AppSnackbarHost(
                    snackBarHostState = snackbarHostState
                )
            }
        },
        bottomBar = {
            AppNavigationBar(
                barItems = {
                    AppNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.review,
                                iconRes = R.drawable.review,
                                selectedIconRes = R.drawable.review_fill,
                            )
                        },
                        isSelected = currentPage is Route.Page.Review
                    ) {
                        appRouter.openPage(Route.Page.Review)
                    }

                    AppNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.schedule,
                                iconRes = R.drawable.schedule,
                                selectedIconRes = R.drawable.schedule_fill,
                            )
                        },
                        isSelected = currentPage is Route.Page.Schedule,
                        onClick = {
                            if (currentPage is Route.Page.Schedule) {

                            } else {
                                appRouter.openPage(Route.Page.Schedule)
                            }
                        }
                    )
                    AppNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.tasks,
                                iconRes = R.drawable.tasks,
                                selectedIconRes = R.drawable.tasks_fill,
                            )
                        },
                        isSelected = currentPage is Route.Page.Tasks,
                    ) {
                        appRouter.openPage(Route.Page.Tasks)
                    }

                    AppNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.settings,
                                iconRes = R.drawable.settings,
                                selectedIconRes = R.drawable.settings_fill,
                            )
                        },
                        showBadge = appState.updatesAvailable,
                        isSelected = currentPage is Route.Page.Settings,
                    ) {
                        appRouter.openPage(Route.Page.Settings)
                    }
                },
                isDarkTheme = appSettings.themePreferences.theme.isDarkTheme()
            )
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

                entry<Route.Page.Schedule> {
                    ScreenSchedule(
                        appSettings = appSettings,

                        contentPadding = padding,
                        isToday = isToday,
                        launchImport = { array ->
                            importLauncher.launch(array)
                        },
                        snackbarHostState = snackbarHostState,
                        onOpenDialog = { dialog ->
                            appRouter.openDialog(dialog)
                        }
                    ) { value ->
                        preferencesViewModel.onSetScheduleView(value)
                    }
                }

                entry<Route.Page.Tasks> {
                    TasksScreen(
                        contentPadding = padding,
                        isToday = isToday,
                    ) { dialog ->
                        appRouter.openDialog(dialog)
                    }
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        updatesAvailable = appState.updatesAvailable,
                        appSettings = appSettings,

                        preferencesViewModel = preferencesViewModel,
                        contentPadding = padding,
                        launch = { array ->
                            importLauncher.launch(array)
                        }
                    ) { uri ->
                        mainViewModel.onAction(
                            MainAction.ExportData(uri)
                        )
                    }
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
                    AddTaskDialog {
                        appRouter.onBack()
                    }
                }

                entry<Route.Dialog.EditTaskDialog> { dialog ->
                    EditTaskDialog(
                        editTaskDialog = dialog
                    ) {
                        appRouter.onBack()
                    }
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