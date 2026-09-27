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
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.BarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBar
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host.AppSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.components.ui_event.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.common.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumDialog
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDialog
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.AddScheduleScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.HiddenEventsScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.RenameScheduleScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.search.ui.SearchDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task.AddTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.EditTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.screen.TasksScreen
import com.egormelnikoff.schedulerutmiit.ui.setting_screen.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel
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
    val appState by mainViewModel.appState.collectAsStateWithLifecycle()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        mainViewModel.importData(
            uri = uri,
            onSuccess = {
                appRouter.navigateToStartRage()
            }
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
                        isSelected = currentPage is Route.Page.Review,
                        onClick = {
                            appRouter.openPage(Route.Page.Review)
                        }
                    )
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
                            appRouter.openPage(Route.Page.Schedule())
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
                        isDarkTheme = appSettings.themePreferences.theme.isDarkTheme(),
                        usedPhoto = appSettings.usedImageInReview,
                        contentPadding = padding,
                        onNavigateToStartPage = {
                            appRouter.navigateToStartRage()
                        }
                    ) { dialog ->
                        appRouter.openDialog(dialog)
                    }
                }

                entry<Route.Page.Schedule> { route ->

                    ScreenSchedule(
                        route = route,
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
                        mainViewModel.exportData(uri)
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
                    NewsDialog(
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
                    SearchDialog { navigateSchedule ->
                        appRouter.openPage(Route.Page.Schedule(navigateSchedule))
                        appRouter.onBack()
                    }
                }
                entry<Route.Dialog.CurriculumDialog> {
                    CurriculumDialog()
                }
                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleScreen()
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { renameDialog ->
                    RenameScheduleScreen(renameDialog)
                }
                entry<Route.Dialog.HiddenEventsDialog> { hiddenEventsDialog ->
                    HiddenEventsScreen(hiddenEventsDialog)
                }
            }
        )
    }
}