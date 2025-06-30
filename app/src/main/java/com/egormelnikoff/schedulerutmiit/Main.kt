package com.egormelnikoff.schedulerutmiit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.data.Event
import com.egormelnikoff.schedulerutmiit.ui.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScheduleData
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateDefaultDate
import com.egormelnikoff.schedulerutmiit.ui.search.SearchScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_models.SettingsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun Main(
    scheduleViewModel: ScheduleViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
    newsViewModel: NewsViewModel,
    preferencesDataStore: DataStore,

    appSettings: AppSettings,
) {
    val barItems = arrayOf(
        BarItem(
            title = LocalContext.current.getString(R.string.search),
            image = ImageVector.vectorResource(R.drawable.search),
            route = Routes.Search.route
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.schedule),
            image = ImageVector.vectorResource(R.drawable.schedule),
            route = Routes.Schedule.route
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.news),
            image = ImageVector.vectorResource(R.drawable.news),
            route = Routes.News.route
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.settings),
            image = ImageVector.vectorResource(R.drawable.settings),
            route = Routes.Settings.route
        )
    )

    var currentRoute by rememberSaveable { mutableStateOf(Routes.Schedule.route) }
    val snackbarHostState = remember { SnackbarHostState() }


    val searchState = searchViewModel.stateSearch.collectAsState().value
    var query by remember { mutableStateOf("") }

    val scheduleState = scheduleViewModel.stateSchedule.collectAsState().value
    val today by remember { mutableStateOf(LocalDate.now()) }
    val scheduleListState = rememberLazyListState()
    val schedulesData: MutableMap<String, ScheduleData> = mutableMapOf()
    if (scheduleState is ScheduleState.Loaded) {
        scheduleState.namedSchedule.schedules.forEach { scheduleFormatted ->
            val weeksCount by remember(
                scheduleState.namedSchedule.namedScheduleEntity.apiId,
                scheduleFormatted.scheduleEntity.timetableId
            ) {
                mutableIntStateOf(
                    ChronoUnit.WEEKS.between(
                        scheduleFormatted.scheduleEntity.startDate,
                        scheduleFormatted.scheduleEntity.endDate
                    ).plus(1).toInt()
                )
            }
            val params by remember(
                scheduleState.namedSchedule.namedScheduleEntity.apiId,
                scheduleFormatted.scheduleEntity.timetableId
            ) {
                mutableStateOf(
                    calculateDefaultDate(
                        today, weeksCount, scheduleFormatted.scheduleEntity
                    )
                )
            }

            val pagerDaysState = rememberPagerState(
                pageCount = { weeksCount * 7 },
                initialPage = params.third
            )

            val pagerWeeksState = rememberPagerState(
                pageCount = { weeksCount },
                initialPage = params.second
            )

            var selectedDate by remember {
                mutableStateOf(
                    params.first
                )
            }

            val scheduleData = ScheduleData(
                selectedDate = selectedDate,
                selectDate = { newValue -> selectedDate = newValue },
                pagerDaysState = pagerDaysState,
                pagerWeeksState = pagerWeeksState,

                defaultDate = params.first,
                daysStartIndex = params.third,
                weeksStartIndex = params.second,
                weeksCount = weeksCount
            )
            schedulesData[scheduleFormatted.scheduleEntity.timetableId] = scheduleData
            LaunchedEffect(scheduleState.selectedSchedule?.scheduleEntity) {
                scheduleListState.scrollToItem(0)
            }
            LaunchedEffect(scheduleState.namedSchedule.namedScheduleEntity.apiId) {
                scheduleData.pagerDaysState.scrollToPage(params.third)
            }
        }
    }
    var showDialogEvent by remember { mutableStateOf(false) }
    var displayedEvent by remember { mutableStateOf<Event?>(null) }
    //var showDialogAddEvent by remember { mutableStateOf<Long?>(null) }

    val stateNewsList = newsViewModel.stateNewsList.collectAsState().value
    val stateNews = newsViewModel.stateNews.collectAsState().value
    var showDialogNews by remember { mutableStateOf(false) }
    val newsListState = rememberLazyListState()

    val schedulesState = scheduleViewModel.stateSchedules.collectAsState().value
    val appInfoState = settingsViewModel.stateAuthor.collectAsState().value
    var showDialogSchedules by remember { mutableStateOf(false) }
    var showDialogInfo by remember { mutableStateOf(false) }
    val settingsListState = rememberScrollState()
    //var showDialogAddSchedule by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navigationBarItemColors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIconColor = Color.Unspecified,
                    disabledTextColor = Color.Unspecified
                )
                barItems.forEach { barItem ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = barItem.image,
                                contentDescription = barItem.title
                            )
                        },
                        label = {
                            Text(
                                text = barItem.title,
                                fontSize = 10.sp
                            )
                        },
                        selected = currentRoute == barItem.route,
                        colors = navigationBarItemColors,
                        onClick = {
                            if (currentRoute == barItem.route) {
                                when (barItem.route) {
                                    Routes.Schedule.route -> {
                                        showDialogEvent = false
                                    }
                                    Routes.Settings.route -> {
                                        showDialogInfo = false
                                        //showDialogAddSchedule = false
                                        showDialogSchedules = false
                                    }

                                    Routes.Search.route -> {
                                        query = ""
                                        searchViewModel.clearData()
                                    }

                                    Routes.News.route -> {
                                        showDialogNews = false
                                    }
                                }
                            } else {
                                currentRoute = barItem.route
                            }
                        }
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (currentRoute) {
                Routes.Search.route -> {
                    SearchScreen(
                        navigateToSchedule = {
                            currentRoute = Routes.Schedule.route
                        },
                        onQueryChanged = { newValue -> query = newValue },
                        query = query,
                        searchViewModel = searchViewModel,
                        scheduleViewModel = scheduleViewModel,
                        searchState = searchState,
                        paddingValues = paddingValues
                    )
                }

                Routes.Schedule.route -> {
                    ScreenSchedule(
                        navigateToSearch = { currentRoute = Routes.Search.route },
                        onShowDialogEvent = { newValue -> showDialogEvent = newValue },
                        onSelectDisplayedEvent = { newValue -> displayedEvent = newValue },

                        scheduleViewModel = scheduleViewModel,
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,

                        showDialogEvent = showDialogEvent,
                        displayedEvent = displayedEvent,
                        scheduleState = scheduleState,

                        snackbarHostState = snackbarHostState,
                        schedulesData = schedulesData,
                        scheduleListState = scheduleListState,
                        today = today,
                        paddingValues = paddingValues
                    )
                }

                Routes.Settings.route -> {
                    SettingsScreen(
                        scheduleViewModel = scheduleViewModel,
                        settingsViewModel = settingsViewModel,
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,

                        schedulesState = schedulesState,
                        appInfoState = appInfoState,
                        navigateToSearch = { currentRoute = Routes.Search.route },

                        showDialogSchedules = showDialogSchedules,
                        showDialogInfo = showDialogInfo,
                        onShowDialogSchedules = { newValue -> showDialogSchedules = newValue },
                        onShowDialogInfo = { newValue -> showDialogInfo = newValue },
                        //showDialogAddSchedule = showDialogAddSchedule,
                        //onShowDialogAddSchedule = { newValue -> showDialogAddSchedule = newValue },
                        //onShowDialogAddEvent = { newValue -> showDialogAddEvent = newValue },
                        settingsListState = settingsListState,
                        paddingValues = paddingValues
                    )
                }

                Routes.News.route -> {
                    NewsScreen(
                        newsViewModel = newsViewModel,
                        showDialogNews = showDialogNews,
                        onShowDialogNews = { newValue -> showDialogNews = newValue },
                        stateNewsList = stateNewsList,
                        stateNews = stateNews,
                        newsListState = newsListState,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}