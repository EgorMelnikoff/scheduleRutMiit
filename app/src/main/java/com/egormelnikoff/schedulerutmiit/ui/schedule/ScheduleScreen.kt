package com.egormelnikoff.schedulerutmiit.ui.schedule

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScreenSchedule(
    paddingValues: PaddingValues,
    navigateToSearch: () -> Unit,
    onShowDialogEvent: (Pair<Event, EventExtraData?>) -> Unit,

    scheduleViewModel: ScheduleViewModel,
    preferencesDataStore: DataStore,
    appSettings: AppSettings,

    scheduleUiState: ScheduleUiState,
    scheduleCalendarParams: ScheduleCalendarParams,
    scheduleListState: LazyListState,
    today: LocalDate,
) {
    var expandedSchedulesMenu by remember { mutableStateOf(false) }

    when {
        scheduleUiState.isLoading -> {
            LoadingScreen(
                paddingTop = paddingValues.calculateTopPadding(),
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }

        scheduleUiState.isError -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.error),
                subtitle = LocalContext.current.getString(R.string.error_load_schedule),
                paddingTop = paddingValues.calculateTopPadding(),
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }

        scheduleUiState.currentNamedSchedule != null -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    val showLoadDefaultSchedule =
                        scheduleUiState.savedNamedSchedules.isNotEmpty()
                                && !scheduleUiState.currentNamedSchedule.namedScheduleEntity.isDefault
                    ScheduleTopBar(
                        onShowExpandedMenu = { newValue ->
                            expandedSchedulesMenu = newValue
                        },

                        onLoadDefaultSchedule = {
                            scheduleViewModel.loadInitialData(true)
                        },

                        scheduleViewModel = scheduleViewModel,
                        preferencesDataStore = preferencesDataStore,

                        scheduleUiState = scheduleUiState,
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        onShowLoadDefaultScheduleButton = showLoadDefaultSchedule,
                        isScheduleCalendar = appSettings.calendarView
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding())
                ) {
                    AnimatedVisibility(
                        visible = scheduleUiState.isUpdating,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                    ExpandedMenu(
                        scheduleViewModel = scheduleViewModel,
                        scheduleUiState = scheduleUiState,
                        expandedSchedulesMenu = expandedSchedulesMenu,
                        onShowExpandedMenu = { newValue ->
                            expandedSchedulesMenu = newValue
                        }
                    )
                    if (scheduleUiState.currentScheduleData != null && scheduleUiState.currentScheduleEntity != null) {
                        AnimatedContent(
                            targetState = appSettings.calendarView,
                            transitionSpec = {
                                fadeIn() + slideInVertically(
                                    initialOffsetY = {
                                        it / 2
                                    }
                                ) togetherWith fadeOut() +
                                        slideOutVertically(
                                            targetOffsetY = {
                                                it / 2
                                            }
                                        )
                            }
                        ) { targetState ->
                            if (targetState) {
                                ScheduleCalendarView(
                                    onShowDialogEvent = onShowDialogEvent,

                                    isShowCountClasses = appSettings.showCountClasses,
                                    isShortEvent = appSettings.eventView,

                                    scheduleEntity = scheduleUiState.currentScheduleEntity,
                                    eventsExtraData = scheduleUiState.currentScheduleData.eventsExtraData,
                                    eventsByWeekAndDays = scheduleUiState.currentScheduleData.eventsForCalendar,
                                    today = today,
                                    paddingBottom = paddingValues.calculateBottomPadding(),
                                    scheduleCalendarParams = scheduleCalendarParams,
                                    scheduleData = scheduleUiState.currentScheduleData
                                )
                            } else {
                                ScheduleListView(
                                    onShowDialogEvent = onShowDialogEvent,
                                    isShortEvent = appSettings.eventView,
                                    eventsForList = scheduleUiState.currentScheduleData.eventForList,
                                    eventsExtraData = scheduleUiState.currentScheduleData.eventsExtraData,
                                    scheduleListState = scheduleListState,
                                    paddingBottom = paddingValues.calculateBottomPadding()
                                )
                            }
                        }
                    } else {
                        Empty(
                            title = "¯\\_(ツ)_/¯",
                            subtitle = LocalContext.current.getString(R.string.empty_here),
                            isBoldTitle = false,
                            paddingBottom = paddingValues.calculateBottomPadding()
                        )
                    }
                }
            }
        }


        scheduleUiState.savedNamedSchedules.isEmpty() -> {
            ErrorScreen(
                title = LocalContext.current.getString(R.string.no_saved_schedule),
                subtitle = LocalContext.current.getString(R.string.empty_base),
                buttonTitle = LocalContext.current.getString(R.string.search),
                imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                action = { navigateToSearch() },
                paddingBottom = paddingValues.calculateBottomPadding()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopBar(
    scheduleUiState: ScheduleUiState,
    scheduleViewModel: ScheduleViewModel,

    onShowExpandedMenu: (Boolean) -> Unit,
    expandedSchedulesMenu: Boolean,

    onLoadDefaultSchedule: () -> Unit,
    onShowLoadDefaultScheduleButton: Boolean,

    preferencesDataStore: DataStore,
    isScheduleCalendar: Boolean
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val rotationAngle by animateFloatAsState(
        targetValue = if (expandedSchedulesMenu) 180f else 0f
    )

    TopAppBar(
        navigationIcon = {
            if (onShowLoadDefaultScheduleButton) {
                IconButton(
                    onClick = {
                        onLoadDefaultSchedule()
                    },
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.back),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .let {
                        if (scheduleUiState.currentNamedSchedule!!.schedules.size > 1) {
                            it
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    onClick = {
                                        onShowExpandedMenu(!expandedSchedulesMenu)
                                    }
                                )

                        } else {
                            it
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column {
                    Text(
                        text = scheduleUiState.currentNamedSchedule!!.namedScheduleEntity.shortName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (scheduleUiState.currentScheduleEntity != null) {
                        Text(
                            text = scheduleUiState.currentScheduleEntity.typeName,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (scheduleUiState.currentNamedSchedule!!.schedules.size > 1) {
                    Icon(
                        modifier = Modifier.graphicsLayer(
                            rotationZ = rotationAngle
                        ),
                        imageVector = ImageVector.vectorResource(R.drawable.down),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            if (scheduleUiState.currentNamedSchedule!!.schedules.isNotEmpty() && scheduleUiState.currentScheduleEntity != null) {
                IconButton(
                    onClick = {
                        val url = scheduleUiState.currentScheduleEntity.downloadUrl
                        val intent = Intent(Intent.ACTION_VIEW, url?.toUri())
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.download),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            if (isScheduleCalendar) {
                                preferencesDataStore.setScheduleView(false)
                            } else {
                                preferencesDataStore.setScheduleView(true)
                            }
                        }

                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = if (isScheduleCalendar) ImageVector.vectorResource(R.drawable.list) else ImageVector.vectorResource(
                            R.drawable.calendar
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            IconToggleButton(
                checked = scheduleUiState.isSaved,
                enabled = if (scheduleUiState.isSaved) true else scheduleUiState.isSavingAvailable,
                colors = IconToggleButtonColors(
                    containerColor = Color.Unspecified,
                    checkedContainerColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,

                    contentColor = MaterialTheme.colorScheme.onBackground,
                    checkedContentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ),
                onCheckedChange = {
                    if (scheduleUiState.isSaved) {
                        scheduleViewModel.deleteNamedSchedule(
                            primaryKey = scheduleUiState.currentNamedSchedule.namedScheduleEntity.id,
                            isDefault = scheduleUiState.currentNamedSchedule.namedScheduleEntity.isDefault
                        )
                    } else {
                        scheduleViewModel.saveCurrentNamedSchedule()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = if (scheduleUiState.isSaved) ImageVector.vectorResource(R.drawable.save_fill) else ImageVector.vectorResource(
                        R.drawable.save
                    ),
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun ExpandedMenu(
    scheduleViewModel: ScheduleViewModel,
    scheduleUiState: ScheduleUiState,
    expandedSchedulesMenu: Boolean,
    onShowExpandedMenu: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = expandedSchedulesMenu,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
            scheduleUiState.currentNamedSchedule!!.schedules.forEach { schedule ->
                val scale by animateFloatAsState(
                    targetValue = if (schedule.scheduleEntity.isDefault) 1f else 0f
                )
                ExpandedMenuItem(
                    onClick = {
                        scheduleViewModel.selectDefaultSchedule(
                            primaryKeySchedule = schedule.scheduleEntity.id,
                            timetableId = schedule.scheduleEntity.timetableId
                        )
                    },
                    title = {
                        Column {
                            Text(
                                text = schedule.scheduleEntity.typeName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = schedule.scheduleEntity.startDate.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ),
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    modifier = Modifier.size(12.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.forward),
                                    contentDescription = null
                                )
                                Text(
                                    text = schedule.scheduleEntity.endDate.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ),
                                    maxLines = 1,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    scale = scale,
                    trailingIcon = ImageVector.vectorResource(R.drawable.check),
                    verticalPadding = 4,
                    trailingIconColor = MaterialTheme.colorScheme.primary
                )
            }
            ExpandedMenuItem(
                onClick = {
                    onShowExpandedMenu(false)
                },
                title = {
                    Text(
                        text = LocalContext.current.getString(R.string.collapse),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = ImageVector.vectorResource(R.drawable.up),
                verticalPadding = 4
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun ExpandedMenuItem(
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    title: @Composable (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    scale: Float? = null,
    verticalPadding: Int,
    trailingIconColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = verticalPadding.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            title?.invoke()
        }
        if (trailingIcon != null) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .let {
                        if (scale != null)
                            it.graphicsLayer(scaleX = scale, scaleY = scale)
                        else it
                    },
                imageVector = trailingIcon,
                contentDescription = null,
                tint = trailingIconColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun calculateFirstDayOfWeek(date: LocalDate): LocalDate {
    return date.minusDays(date.dayOfWeek.value - 1L)
}