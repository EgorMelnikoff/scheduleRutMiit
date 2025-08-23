package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.repos.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.repos.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.ui.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.news.viewmodel.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScheduleData
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateDefaultDate
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleState
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.search.SearchScreen
import com.egormelnikoff.schedulerutmiit.ui.search.viewmodel.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel.SettingsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class BarItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

sealed class Routes(val route: String) {
    data object Search : Routes("search")
    data object Schedule : Routes("schedule")
    data object News : Routes("news")
    data object Settings : Routes("settings")
}

@Composable
fun Main(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    newsViewModel: NewsViewModel,
    settingsViewModel: SettingsViewModel,
    preferencesDataStore: DataStore,

    appSettings: AppSettings,
) {
    val barItems = arrayOf(
        BarItem(
            title = LocalContext.current.getString(R.string.search),
            icon = ImageVector.vectorResource(R.drawable.search),
            route = Routes.Search.route
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.schedule),
            icon = ImageVector.vectorResource(R.drawable.schedule),
            route = Routes.Schedule.route
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.news),
            icon = ImageVector.vectorResource(R.drawable.news),
            route = Routes.News.route
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.settings),
            icon = ImageVector.vectorResource(R.drawable.settings),
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
                        calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.startDate),
                        calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.endDate)
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

            LaunchedEffect(scheduleData.selectedDate) {
                val targetPage = ChronoUnit.DAYS.between(
                    scheduleFormatted.scheduleEntity.startDate,
                    scheduleData.selectedDate
                ).toInt()

                if (scheduleData.pagerDaysState.currentPage != targetPage) {
                    scheduleData.pagerDaysState.scrollToPage(targetPage)
                }
            }

            LaunchedEffect(scheduleData.pagerDaysState.currentPage) {
                val newSelectedDate =
                    scheduleFormatted.scheduleEntity.startDate.plusDays(scheduleData.pagerDaysState.currentPage.toLong())
                scheduleData.selectDate(newSelectedDate)

                val targetWeekIndex = ChronoUnit.WEEKS.between(
                    calculateFirstDayOfWeek(scheduleFormatted.scheduleEntity.startDate),
                    calculateFirstDayOfWeek(newSelectedDate)
                ).toInt()

                if (scheduleData.pagerWeeksState.currentPage != targetWeekIndex) {
                    scheduleData.pagerWeeksState.animateScrollToPage(targetWeekIndex)
                }
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
    val appInfoState = settingsViewModel.stateAppInfo.collectAsState().value
    var showDialogSchedules by remember { mutableStateOf(false) }
    var showDialogInfo by remember { mutableStateOf(false) }
    val settingsListState = rememberScrollState()
    //var showDialogAddSchedule by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(
                        top = 12.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .border(
                            0.1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            MaterialTheme.colorScheme.background.copy(
                                alpha = 0.95f
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    barItems.forEach { barItem ->
                        CustomNavigationBarItem(
                            icon = barItem.icon,
                            title = barItem.title,
                            isSelected = currentRoute == barItem.route,
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
                                            searchViewModel.setDefaultSearchState()
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
//                    Button(
//                        modifier = Modifier
//                            .height(56.dp)
//                            .width(56.dp),
//                        onClick = {},
//                        shape = CircleShape,
//                        colors = ButtonDefaults.buttonColors().copy(
//                            containerColor = MaterialTheme.colorScheme.primary,
//                            contentColor = MaterialTheme.colorScheme.onPrimary
//                        ),
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.add),
//                            contentDescription = null
//                        )
//                    }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.error,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        text = data.visuals.message,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    IconButton(
                        onClick = {
                            data.dismiss()
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.clear),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel,
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

@Composable
fun CustomNavigationBarItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                onClick = onClick
            )
            .let {
                if (isSelected) {
                    it.background(MaterialTheme.colorScheme.primary)
                } else it
            }
            .padding(8.dp)
            .width(52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontSize = 8.sp,
            fontWeight = if (isSelected) FontWeight.Bold
            else FontWeight.Normal,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}