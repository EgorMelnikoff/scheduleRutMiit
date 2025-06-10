package com.egormelnikoff.schedulerutmiit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
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
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScheduleData
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateDefaultDate
import com.egormelnikoff.schedulerutmiit.ui.search.SearchScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.view_models.SettingsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun Main(
    scheduleViewModel: ScheduleViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
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
    val lazyListState = rememberLazyListState()
    val schedulesData: MutableMap<String, ScheduleData> = mutableMapOf()
    if (scheduleState is ScheduleState.Loaded) {
        LaunchedEffect(scheduleState.selectedSchedule?.scheduleEntity) {
            lazyListState.scrollToItem(0)
        }
        scheduleState.namedSchedule.schedules.forEach { scheduleFormatted ->
            val weeksCount by remember(scheduleState.namedSchedule.namedScheduleEntity.apiId, scheduleFormatted.scheduleEntity.timetableId) {
                mutableIntStateOf(
                    ChronoUnit.WEEKS.between(
                        scheduleFormatted.scheduleEntity.startDate,
                        scheduleFormatted.scheduleEntity.endDate
                    ).plus(1).toInt()
                )
            }
            val params by remember(scheduleState.namedSchedule.namedScheduleEntity.apiId, scheduleFormatted.scheduleEntity.timetableId) {
                mutableStateOf(
                    calculateDefaultDate(
                        today, weeksCount, scheduleFormatted.scheduleEntity
                    )
                )
            }

            println(params.third)

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

            LaunchedEffect(scheduleState.namedSchedule.namedScheduleEntity.apiId) {
                scheduleData.pagerDaysState.scrollToPage(params.third)
            }
            schedulesData[scheduleFormatted.scheduleEntity.timetableId] = scheduleData
        }
    }
    var showDialogComments by rememberSaveable { mutableStateOf(false) }

    val schedulesState = scheduleViewModel.stateSchedules.collectAsState().value
    val appInfoState = settingsViewModel.stateAuthor.collectAsState().value
    var showDialogSchedules by remember { mutableStateOf(false) }
    var showDialogInfo by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navigationBarItemColors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
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
                                    Routes.Settings.route -> {
                                        showDialogInfo = false
                                        showDialogSchedules = false
                                    }

                                    Routes.Search.route -> {
                                        query = ""
                                        searchViewModel.clearData()
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
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
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
                        searchState = searchState
                    )
                }

                Routes.Schedule.route -> {
                    ScreenSchedule(
                        scheduleViewModel = scheduleViewModel,
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,
                        navigateToSearch = { currentRoute = Routes.Search.route },
                        showDialogComments = showDialogComments,
                        scheduleState = scheduleState,
                        onShowDialogComments = { newValue -> showDialogComments = newValue },
                        snackbarHostState = snackbarHostState,
                        schedulesData = schedulesData,
                        lazyListState = lazyListState,
                        today = today
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
                        onShowDialogInfo = { newValue -> showDialogInfo = newValue }
                    )
                }
            }
        }
    }
}