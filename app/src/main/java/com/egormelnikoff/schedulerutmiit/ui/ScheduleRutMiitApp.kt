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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.ScheduleView
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BarItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomNavigationBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomNavigationBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.elements.UiEventProcessor
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarData
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.rememberCalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumDialog
import com.egormelnikoff.schedulerutmiit.news.ui.NewsDialog
import com.egormelnikoff.schedulerutmiit.news.ui.NewsScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.EditEventDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.AddScheduleDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.HiddenEventsDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.RenameDialog
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review.ReviewScreen
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.view_model.state.NamedScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.search.ui.SearchDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task.AddTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.EditTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.screen.TasksScreen
import com.egormelnikoff.schedulerutmiit.tasks.ui.screen.view_model.TaskViewModel
import com.egormelnikoff.schedulerutmiit.ui.setting_screen.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ScheduleRutMiitApp(
    preferencesViewModel: PreferencesViewModel,
    appSettings: AppSettings
) {
    val scheduleViewModel = hiltViewModel<ScheduleViewModel>()
    val mainViewModel = hiltViewModel<MainViewModel>()
    val taskViewModel = hiltViewModel<TaskViewModel>()

    val hourlyDateTime by mainViewModel.hourlyDateTime.collectAsStateWithLifecycle()

    val namedScheduleState by scheduleViewModel.namedScheduleState.collectAsStateWithLifecycle()
    val scheduleCalendarState = rememberScheduleCalendarState(namedScheduleState)

    val tasksCalendarData = remember {
        CalendarData(
            hourlyDateTime.toLocalDate().minusYears(5),
            hourlyDateTime.toLocalDate().plusYears(5)
        )
    }
    val tasksCalendarState = rememberCalendarState(tasksCalendarData)

    val appBackStack = remember { AppBackStack(Route.Page.Schedule) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scheduleListState = rememberLazyListState()
    val settingsListState = rememberLazyStaggeredGridState()

    val reviewUiState = ReviewUiState()

    UiEventProcessor(
        mainViewModel.uiEvent,
        snackbarHostState
    )

    UiEventProcessor(
        scheduleViewModel.uiEvent,
        snackbarHostState
    )

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    scheduleCalendarState = scheduleCalendarState,
                    scheduleListState = scheduleListState,
                    reviewUiState = reviewUiState,
                    tasksCalendarState = tasksCalendarState,

                    mainViewModel = mainViewModel,
                    scheduleViewModel = scheduleViewModel,
                    preferencesViewModel = preferencesViewModel,
                    taskViewModel = taskViewModel,

                    namedScheduleState = namedScheduleState,

                    appBackStack = appBackStack,
                    snackbarHostState = snackbarHostState,
                    settingsListState = settingsListState,

                    appSettings = appSettings
                ) { date ->
                    hourlyDateTime.toLocalDate() == date
                }
            },
            snackbarHostState = snackbarHostState,
            appBackStack = appBackStack,
            tasksCalendarData = tasksCalendarData
        ) { name, apiId, type ->
            scheduleViewModel.fetchNamedSchedule(
                name, apiId, type
            )
        }
    }
}


@Composable
fun PageHost(
    mainViewModel: MainViewModel,
    scheduleViewModel: ScheduleViewModel,
    preferencesViewModel: PreferencesViewModel,
    taskViewModel: TaskViewModel,

    namedScheduleState: NamedScheduleState,

    appBackStack: AppBackStack,
    snackbarHostState: SnackbarHostState,
    settingsListState: LazyStaggeredGridState,

    scheduleCalendarState: CalendarState?,
    tasksCalendarState: CalendarState,
    scheduleListState: LazyListState,
    reviewUiState: ReviewUiState,

    appSettings: AppSettings,
    isToday: (LocalDate) -> Boolean
) {
    val scope = rememberCoroutineScope()

    val appState by mainViewModel.appState.collectAsStateWithLifecycle()


    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        mainViewModel.importData(
            uri = uri,
            onSuccess = {
                appBackStack.navigateToStartRage()
            }
        )
    }

    val currentPage = appBackStack.lastPage()

    val navigate: (Route.Page) -> Unit = remember(appBackStack) {
        { page ->
            appBackStack.openPage(page)
        }
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            if (appBackStack.dialogBackStack.size <= 1) {
                CustomSnackbarHost(
                    snackBarHostState = snackbarHostState
                )
            }
        },
        bottomBar = {
            CustomNavigationBar(
                currentPageIndex = appBackStack.lastPage().index,
                barItems = {
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.review,
                                iconRes = R.drawable.review,
                                selectedIconRes = R.drawable.review_fill,
                                page = Route.Page.Review
                            )
                        },
                        isSelected = currentPage == Route.Page.Review,
                        navigate = navigate,
                        onClick = null
                    )
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.schedule,
                                iconRes = R.drawable.schedule,
                                selectedIconRes = R.drawable.schedule_fill,
                                page = Route.Page.Schedule
                            )
                        },
                        navigate = navigate,
                        isSelected = currentPage == Route.Page.Schedule,
                        onClick = scheduleCalendarState?.let {
                            {
                                scope.launch {
                                    when {

                                        namedScheduleState is NamedScheduleState.Loaded && namedScheduleState.scheduleState?.calendarData != null && appSettings.scheduleView == ScheduleView.CALENDAR -> {
                                            scheduleCalendarState.selectInitialDate()
                                        }

                                        appSettings.scheduleView == ScheduleView.LIST -> {
                                            scheduleListState.animateScrollToItem(0)
                                        }
                                    }
                                }
                            }
                        }
                    )
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.tasks,
                                iconRes = R.drawable.tasks,
                                selectedIconRes = R.drawable.tasks_fill,
                                page = Route.Page.Tasks
                            )
                        },
                        isSelected = currentPage == Route.Page.Tasks,
                        navigate = navigate
                    ) {
                        scope.launch {
                            tasksCalendarState.selectInitialDate()
                        }
                    }
                    CustomNavigationBarItem(
                        barItem = remember {
                            BarItem(
                                title = R.string.settings,
                                iconRes = R.drawable.settings,
                                selectedIconRes = R.drawable.settings_fill,
                                page = Route.Page.Settings
                            )
                        },
                        showBadge = appState.updatesAvailable,
                        isSelected = currentPage == Route.Page.Settings,
                        navigate = navigate
                    ) {
                        scope.launch {
                            settingsListState.animateScrollToItem(0)
                        }
                    }
                },
                isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme()
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
            backStack = appBackStack.pageBackStack,
            onBack = {
                appBackStack.onBack()
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
                        reviewUiState = reviewUiState,
                        isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme(),
                        usedPhoto = appSettings.usedImageInReview,
                        contentPadding = padding,
                        onNavigateToStartPage = {
                            appBackStack.navigateToStartRage()
                        }
                    ) { dialog ->
                        appBackStack.openDialog(dialog)
                    }
                }

                entry<Route.Page.Schedule> {
                    val screenState by scheduleViewModel.screenState.collectAsStateWithLifecycle()

                    ScreenSchedule(
                        namedScheduleState = namedScheduleState,
                        screenState = screenState,

                        scheduleCalendarState = scheduleCalendarState,
                        scheduleListState = scheduleListState,
                        appSettings = appSettings,
                        scheduleViewModel = scheduleViewModel,

                        contentPadding = padding,
                        isToday = isToday,
                        launchImport = { array ->
                            importLauncher.launch(array)
                        },
                        onOpenDialog = { dialog ->
                            appBackStack.openDialog(dialog)
                        }
                    ) { value ->
                        preferencesViewModel.onSetScheduleView(value)
                    }
                }

                entry<Route.Page.Tasks> {
                    val tasks by taskViewModel.tasks.collectAsStateWithLifecycle()

                    TasksScreen(
                        calendarState = tasksCalendarState,
                        tasks = tasks,
                        contentPadding = padding,
                        isToday = isToday,
                        onTaskAction = { taskAction ->
                            taskViewModel.taskAction(taskAction)
                        },
                    ) { dialog ->
                        appBackStack.openDialog(dialog)
                    }
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        settingsListState = settingsListState,
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
    appBackStack: AppBackStack,
    snackbarHostState: SnackbarHostState,
    tasksCalendarData: CalendarData,
    fetchNamedSchedule: (String, Int, NamedScheduleType) -> Unit
) {
    Scaffold(
        snackbarHost = {
            if (appBackStack.dialogBackStack.size > 1) {
                CustomSnackbarHost(
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
            backStack = appBackStack.dialogBackStack,
            onBack = {
                appBackStack.onBack()
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
                    EventDialog(
                        eventDialog = dialog,
                        fetchNamedSchedule = { name, apiId, type ->
                            fetchNamedSchedule(name, apiId, type)
                        },
                        navigateToStartPage = {
                            appBackStack.navigateToStartRage()
                        },
                        navigateToEditEventDialog = { editDialog ->
                            appBackStack.openDialog(editDialog)
                        }
                    ) {
                        appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.NewsList> {
                    NewsScreen { newsId ->
                        appBackStack.openDialog(
                            Route.Dialog.NewsDialog(newsId)
                        )
                    }
                }

                entry<Route.Dialog.NewsDialog> { dialog ->
                    NewsDialog(
                        newsDialog = dialog
                    ) {
                        appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.AddTaskDialog> {
                    AddTaskDialog(
                        calendarData = tasksCalendarData,
                    ) {
                        appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.EditTaskDialog> { dialog ->
                    EditTaskDialog(
                        editTaskDialog = dialog
                    ) {
                        appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.EditEventDialog> { dialog ->
                    EditEventDialog(
                        editEventDialog = dialog
                    ) {
                        appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.SearchDialog> {
                    SearchDialog { name, apiId, type ->
                        fetchNamedSchedule(name, apiId, type)
                        appBackStack.openPage(Route.Page.Schedule)
                        appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.CurriculumDialog> {
                    CurriculumDialog()
                }
                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleDialog {
                        appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { dialog ->
                    RenameDialog(
                        renameDialog = dialog,
                    ) {
                        appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.HiddenEventsDialog> { dialog ->
                    HiddenEventsDialog(
                        hiddenEventsDialog = dialog
                    ) {
                        appBackStack.onBack()
                    }
                }
            }
        )
    }
}


@Composable
fun rememberScheduleCalendarState(
    namedScheduleState: NamedScheduleState
): CalendarState? {
    val calendarData = (namedScheduleState as? NamedScheduleState.Loaded)
        ?.scheduleState
        ?.calendarData

    return if (calendarData != null) {
        rememberCalendarState(calendarData)
    } else {
        null
    }
}