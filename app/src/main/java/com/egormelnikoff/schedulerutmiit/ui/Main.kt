package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.ui.news.NewsDialog
import com.egormelnikoff.schedulerutmiit.ui.news.NewsScreen
import com.egormelnikoff.schedulerutmiit.ui.news.viewmodel.NewsViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.EventDialog
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScheduleCalendarParams
import com.egormelnikoff.schedulerutmiit.ui.schedule.ScreenSchedule
import com.egormelnikoff.schedulerutmiit.ui.schedule.calculateFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.UiEvent
import com.egormelnikoff.schedulerutmiit.ui.search.SearchScreen
import com.egormelnikoff.schedulerutmiit.ui.search.viewmodel.SearchViewModel
import com.egormelnikoff.schedulerutmiit.ui.settings.InfoDialog
import com.egormelnikoff.schedulerutmiit.ui.settings.SchedulesDialog
import com.egormelnikoff.schedulerutmiit.ui.settings.SettingsScreen
import com.egormelnikoff.schedulerutmiit.ui.settings.viewmodel.SettingsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
            route = Routes.Search
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.schedule),
            icon = ImageVector.vectorResource(R.drawable.schedule),
            route = Routes.Schedule
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.news),
            icon = ImageVector.vectorResource(R.drawable.news),
            route = Routes.NewsList
        ),
        BarItem(
            title = LocalContext.current.getString(R.string.settings),
            icon = ImageVector.vectorResource(R.drawable.settings),
            route = Routes.Settings
        )
    )
    val appBackStack = remember {
        AppBackStack(
            startRoute = Routes.Schedule
        )
    }

    val searchState = searchViewModel.stateSearch.collectAsState().value
    val scheduleUiState = scheduleViewModel.uiState.collectAsState().value
    val newsUiState = newsViewModel.uiState.collectAsState().value
    val appInfoState = settingsViewModel.stateAppInfo.collectAsState().value

    val snackBarHostState = remember { SnackbarHostState() }
    val scheduleListState = rememberLazyListState()
    val newsListState = rememberLazyListState()
    val settingsListState = rememberScrollState()

    var query by remember { mutableStateOf("") }
    val today by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(key1 = Unit) {
        scheduleViewModel.uiEvent.collect { info ->
            when (info) {
                is UiEvent.ShowErrorMessage -> {
                    snackBarHostState.showSnackbar(
                        message = info.message,
                        duration = SnackbarDuration.Long
                    )
                }

                is UiEvent.ShowInfoMessage -> {
                    snackBarHostState.showSnackbar(
                        message = info.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    val pagerDaysState = rememberPagerState(
        pageCount = { scheduleUiState.currentScheduleData?.weeksCount?.times(7) ?: 0 },
        initialPage = scheduleUiState.currentScheduleData?.daysStartIndex ?: 0
    )

    val pagerWeeksState = rememberPagerState(
        pageCount = { scheduleUiState.currentScheduleData?.weeksCount ?: 0 },
        initialPage = scheduleUiState.currentScheduleData?.weeksStartIndex ?: 0
    )

    var selectedDate by remember(
        scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.apiId
    ) {
        mutableStateOf(
            scheduleUiState.currentScheduleData?.defaultDate ?: LocalDate.now()
        )
    }

    LaunchedEffect(
        scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.apiId,
        scheduleUiState.currentScheduleEntity?.timetableId
    ) {
        scheduleListState.scrollToItem(0)
    }

    LaunchedEffect(scheduleUiState.currentNamedSchedule?.namedScheduleEntity?.apiId) {
        pagerDaysState.scrollToPage(scheduleUiState.currentScheduleData?.weeksStartIndex ?: 0)
    }

    LaunchedEffect(selectedDate) {
        if (scheduleUiState.currentScheduleEntity != null) {
            val targetPage = ChronoUnit.DAYS.between(
                scheduleUiState.currentScheduleEntity.startDate,
                selectedDate
            ).toInt()

            if (pagerDaysState.currentPage != targetPage) {
                pagerDaysState.scrollToPage(targetPage)
            }
        }
    }

    LaunchedEffect(pagerDaysState.currentPage) {
        if (scheduleUiState.currentScheduleEntity != null) {
            val newSelectedDate =
                scheduleUiState.currentScheduleEntity.startDate.plusDays(
                    pagerDaysState.currentPage.toLong()
                )
            selectedDate = newSelectedDate

            val targetWeekIndex = ChronoUnit.WEEKS.between(
                calculateFirstDayOfWeek(scheduleUiState.currentScheduleEntity.startDate),
                calculateFirstDayOfWeek(newSelectedDate)
            ).toInt()

            if (pagerWeeksState.currentPage != targetWeekIndex) {
                pagerWeeksState.animateScrollToPage(targetWeekIndex)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            CustomNavigationBar(
                appBackStack = appBackStack,
                visible = !appBackStack.last().isDialog,
                barItems = barItems
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
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
                        color = MaterialTheme.colorScheme.onSurface
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
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = appBackStack.backStack,
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
                fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
            },
            entryProvider = entryProvider {
                entry<Routes.Schedule> {
                    ScreenSchedule(
                        navigateToSearch = {
                            appBackStack.navigateToPage(Routes.Search)
                        },
                        onShowDialogEvent = { value ->
                            appBackStack.navigateToDialog(
                                Routes.EventDialog(
                                    event = value.first,
                                    eventExtraData = value.second
                                )
                            )
                        },

                        scheduleViewModel = scheduleViewModel,
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,
                        scheduleUiState = scheduleUiState,
                        scheduleListState = scheduleListState,
                        today = today,

                        paddingValues = paddingValues,
                        scheduleCalendarParams = ScheduleCalendarParams(
                            pagerDaysState = pagerDaysState,
                            pagerWeeksState = pagerWeeksState,
                            selectedDate = selectedDate,
                            selectDate = { newValue -> selectedDate = newValue }
                        )
                    )
                }

                entry(Routes.Search) {
                    SearchScreen(
                        navigateToSchedule = {
                            appBackStack.navigateToPage(Routes.Schedule)
                        },
                        onQueryChanged = { newValue -> query = newValue },
                        query = query,
                        scheduleViewModel = scheduleViewModel,
                        searchViewModel = searchViewModel,
                        searchState = searchState,
                        paddingValues = paddingValues
                    )
                }

                entry<Routes.NewsList> {
                    println(newsUiState)
                    NewsScreen(
                        newsViewModel = newsViewModel,
                        onShowDialogNews = {
                            appBackStack.navigateToDialog(Routes.News)
                        },
                        newsUiState = newsUiState,
                        newsLazyListState = newsListState,
                        paddingValues = paddingValues
                    )
                }

                entry(Routes.Settings) {
                    SettingsScreen(
                        preferencesDataStore = preferencesDataStore,
                        appSettings = appSettings,

                        scheduleUiState = scheduleUiState,

                        onShowDialogSchedules = {
                            appBackStack.navigateToDialog(Routes.Schedules)
                        },
                        onShowDialogInfo = {
                            appBackStack.navigateToDialog(Routes.Info)
                        },
                        settingsListState = settingsListState,
                        paddingValues = paddingValues
                    )
                }

                entry<Routes.EventDialog> { key ->
                    EventDialog(
                        onBack = { appBackStack.onBack() },
                        scheduleViewModel = scheduleViewModel,
                        isSavedSchedule = scheduleUiState.isSaved,
                        event = key.event!!,
                        eventExtraData = key.eventExtraData
                    )
                }

                entry(Routes.News) {
                    NewsDialog(
                        newsUiState = newsUiState,
                        paddingValues = paddingValues
                    )
                }

                entry(Routes.Schedules) {
                    SchedulesDialog(
                        onBack = {
                            appBackStack.onBack()
                        },
                        navigateToSearch = {
                            appBackStack.onBack()
                            appBackStack.navigateToPage(Routes.Search)
                        },
                        scheduleViewModel = scheduleViewModel,
                        scheduleUiState = scheduleUiState,
                        paddingValues = paddingValues
                    )
                }

                entry(Routes.Info) {
                    InfoDialog(
                        onBack = { appBackStack.onBack() },
                        appInfoState = appInfoState,
                        paddingValues = paddingValues
                    )
                }
            }
        )
    }
}

@Composable
fun CustomNavigationBar(
    appBackStack: AppBackStack<Routes.Schedule>,
    visible: Boolean,
    barItems: Array<BarItem>
) {
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
        CustomAnimatedVisibility(
            visible = visible
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
                        MaterialTheme.colorScheme.background
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                barItems.forEach { barItem ->
                    CustomNavigationBarItem(
                        icon = barItem.icon,
                        title = barItem.title,
                        isSelected = appBackStack.last() == barItem.route,
                        onClick = {
                            appBackStack.navigateToPage(barItem.route)
                        }
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
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(scale)
            .let {
                if (isSelected) {
                    it.background(
                        MaterialTheme.colorScheme.surface
                    )
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
            tint = if (isSelected) MaterialTheme.colorScheme.primary
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
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CustomAnimatedVisibility(
    visible: Boolean,
    content: @Composable (() -> Unit),
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight }
        )
    ) {
        content.invoke()
    }
}