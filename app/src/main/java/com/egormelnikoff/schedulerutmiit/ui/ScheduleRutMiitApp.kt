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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScreenState
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
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
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.AppUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.search.ui.SearchDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.add_task.AddTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.EditTaskDialog
import com.egormelnikoff.schedulerutmiit.tasks.ui.screen.TasksScreen
import com.egormelnikoff.schedulerutmiit.tasks.ui.screen.view_model.TaskViewModel
import com.egormelnikoff.schedulerutmiit.ui.setting_screen.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_model.MainViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_model.state.AppState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ScheduleRutMiitApp(
    preferencesViewModel: PreferencesViewModel,
    appSettings: AppSettings
) {
    val scheduleViewModel = hiltViewModel<ScheduleViewModel>()
    val mainViewModel = hiltViewModel<MainViewModel>()
    val taskViewModel = hiltViewModel<TaskViewModel>()

    val screenState = scheduleViewModel.screenState.collectAsStateWithLifecycle().value
    val namedScheduleState =
        scheduleViewModel.namedScheduleState.collectAsStateWithLifecycle().value
    val scheduleCalendarState =
        if (namedScheduleState is NamedScheduleState.Loaded && namedScheduleState.scheduleState?.calendarData != null) {
            rememberCalendarState(namedScheduleState.scheduleState!!.calendarData)
        } else null

    val hourlyDateTime = mainViewModel.hourlyDateTime.collectAsStateWithLifecycle().value
    val appState = mainViewModel.appState.collectAsStateWithLifecycle().value


    val tasks = taskViewModel.tasks.collectAsStateWithLifecycle().value
    val tasksCalendarData = remember {
        CalendarData(
            hourlyDateTime.toLocalDate().minusYears(5),
            hourlyDateTime.toLocalDate().plusYears(5)
        )
    }
    val tasksCalendarState = rememberCalendarState(tasksCalendarData)


    val appUiState = AppUiState()
    val scheduleListState = rememberLazyListState()
    val reviewUiState = ReviewUiState()

    UiEventProcessor(
        mainViewModel.uiEvent,
        appUiState.snackBarHostState
    )

    UiEventProcessor(
        scheduleViewModel.uiEvent,
        appUiState.snackBarHostState
    )

    Box(Modifier.fillMaxSize()) {
        RootHost(
            pageHost = {
                PageHost(
                    appUiState = appUiState,
                    scheduleCalendarState = scheduleCalendarState,
                    scheduleListState = scheduleListState,
                    reviewUiState = reviewUiState,
                    tasksCalendarState = tasksCalendarState,

                    mainViewModel = mainViewModel,
                    scheduleViewModel = scheduleViewModel,
                    preferencesViewModel = preferencesViewModel,
                    taskViewModel = taskViewModel,

                    namedScheduleState = namedScheduleState,
                    hourlyDateTime = hourlyDateTime,
                    screenState = screenState,

                    tasks = tasks,
                    appState = appState,

                    appSettings = appSettings
                )
            },

            appUiState = appUiState,
            tasksCalendarState = tasksCalendarState
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
    screenState: ScreenState,

    appState: AppState,
    hourlyDateTime: LocalDateTime,
    tasks: Map<LocalDate, List<Task>>,

    appUiState: AppUiState,
    scheduleCalendarState: CalendarState?,
    tasksCalendarState: CalendarState,
    scheduleListState: LazyListState,
    reviewUiState: ReviewUiState,

    appSettings: AppSettings
) {
    val navigate: (Route.Page) -> Unit = remember {
        { page ->
            appUiState.appBackStack.openPage(page)
        }
    }

    val selectedPage = appUiState.appBackStack.lastPage()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        mainViewModel.importData(
            uri = uri,
            onSuccess = {
                appUiState.appBackStack.navigateToStartRage()
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            if (appUiState.appBackStack.dialogBackStack.size <= 1) {
                CustomSnackbarHost(
                    snackBarHostState = appUiState.snackBarHostState
                )
            }
        },
        bottomBar = {
            CustomNavigationBar(
                currentPageIndex = appUiState.appBackStack.lastPage().index,
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
                        selectedPage = selectedPage,
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
                        selectedPage = selectedPage,
                        navigate = navigate,
                        onClick = scheduleCalendarState?.let {
                            {
                                appUiState.scope.launch {
                                    when {

                                        namedScheduleState is NamedScheduleState.Loaded && namedScheduleState.scheduleState?.calendarData != null && appSettings.scheduleView == ScheduleView.CALENDAR -> {
                                            scheduleCalendarState.selectInitialDate()
                                        }

                                        appSettings.scheduleView == ScheduleView.LIST -> {
                                            scheduleListState.animateScrollToItem(
                                                0
                                            )
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
                        selectedPage = selectedPage,
                        navigate = navigate
                    ) {
                        appUiState.scope.launch {
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
                        selectedPage = selectedPage,
                        showBadge = appState.updatesAvailable,
                        navigate = navigate
                    ) {
                        appUiState.scope.launch {
                            appUiState.settingsListState.animateScrollToItem(0)
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
            backStack = appUiState.appBackStack.pageBackStack,
            onBack = {
                appUiState.appBackStack.onBack()
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
                        appBackStack = appUiState.appBackStack,
                        isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme(),
                        usedPhoto = appSettings.usedImageInReview,
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Schedule> {
                    ScreenSchedule(
                        importLauncher = importLauncher,
                        appUiState = appUiState,

                        namedScheduleState = namedScheduleState,
                        screenState = screenState,

                        hourlyDateTime = hourlyDateTime,

                        scheduleCalendarState = scheduleCalendarState,
                        scheduleListState = scheduleListState,
                        appSettings = appSettings,
                        scheduleViewModel = scheduleViewModel,
                        onSetScheduleView = { value ->
                            preferencesViewModel.onSetScheduleView(value)
                        },
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Tasks> {
                    TasksScreen(
                        appBackStack = appUiState.appBackStack,
                        taskViewModel = taskViewModel,
                        calendarState = tasksCalendarState,
                        tasks = tasks,
                        today = hourlyDateTime.toLocalDate(),
                        externalPadding = padding
                    )
                }

                entry<Route.Page.Settings> {
                    SettingsScreen(
                        appUiState = appUiState,
                        appSettings = appSettings,
                        appState = appState,
                        preferencesViewModel = preferencesViewModel,
                        mainViewModel = mainViewModel,
                        importLauncher = importLauncher,
                        externalPadding = padding
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
    appUiState: AppUiState,
    tasksCalendarState: CalendarState,
    fetchNamedSchedule: (String, Int, NamedScheduleType) -> Unit
) {
    Scaffold(
        snackbarHost = {
            if (appUiState.appBackStack.dialogBackStack.size > 1) {
                CustomSnackbarHost(
                    snackBarHostState = appUiState.snackBarHostState
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
            backStack = appUiState.appBackStack.dialogBackStack,
            onBack = {
                appUiState.appBackStack.onBack()
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
                            appUiState.appBackStack.navigateToStartRage()
                        },
                        navigateToEditEventDialog = { editDialog ->
                            appUiState.appBackStack.openDialog(editDialog)
                        }
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.NewsList> {
                    NewsScreen(
                        onGetNewsById = { id ->
                            appUiState.appBackStack.openDialog(
                                Route.Dialog.NewsDialog(id)
                            )
                        },
                        newsGridListState = appUiState.newsListState
                    )
                }

                entry<Route.Dialog.NewsDialog> { dialog ->
                    NewsDialog(
                        newsDialog = dialog
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.AddTaskDialog> {
                    AddTaskDialog(
                        calendarData = tasksCalendarState.calendarData,
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.EditTaskDialog> { dialog ->
                    EditTaskDialog(
                        editTaskDialog = dialog
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.EditEventDialog> { dialog ->
                    EditEventDialog(
                        editEventDialog = dialog
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }

                entry<Route.Dialog.SearchDialog> {
                    SearchDialog { name, apiId, type ->
                        fetchNamedSchedule(name, apiId, type)
                        appUiState.appBackStack.openPage(Route.Page.Schedule)
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.CurriculumDialog> {
                    CurriculumDialog()
                }
                entry<Route.Dialog.AddScheduleDialog> {
                    AddScheduleDialog {
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.RenameNamedScheduleDialog> { dialog ->
                    RenameDialog(
                        renameDialog = dialog,
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }
                entry<Route.Dialog.HiddenEventsDialog> { dialog ->
                    HiddenEventsDialog(
                        hiddenEventsDialog = dialog
                    ) {
                        appUiState.appBackStack.onBack()
                    }
                }
            }
        )
    }
}