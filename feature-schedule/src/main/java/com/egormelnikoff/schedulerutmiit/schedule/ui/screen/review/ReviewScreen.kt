package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.DayPeriod
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.extension.dayPeriod
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ExpandedItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.core.ui.elements.RowGroup
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.elements.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule.ui.ui_state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.view_model.state.NamedScheduleState
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    namedSchedules: List<NamedSchedule>,
    namedScheduleState: NamedScheduleState,
    reviewUiState: ReviewUiState,
    currentDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    usedPhoto: Boolean,
    isDarkTheme: Boolean,
    externalPadding: PaddingValues
) {
    val topBarHeightPx = with(LocalDensity.current) {
        60.dp.toPx()
    }

    val currentDate = remember(currentDateTime) { currentDateTime.toLocalDate() }
    val dayPeriod = remember(currentDateTime) { currentDateTime.dayPeriod() }

    var namedScheduleDialog by remember { mutableStateOf<NamedSchedule?>(null) }
    var deleteNamedScheduleDialog by remember { mutableStateOf<NamedSchedule?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val overlappedFraction by remember {
        derivedStateOf {
            scrollBehavior.state.overlappedFraction
        }
    }

    val contentColor by animateColorAsState(
        targetValue = if (usedPhoto) {
            lerp(
                start = MaterialTheme.colorScheme.onPrimary,
                stop = MaterialTheme.colorScheme.onBackground,
                fraction = overlappedFraction
            )
        } else MaterialTheme.colorScheme.onBackground,
        label = "TopAppBarContentColor"
    )

    var showTopBarActions by remember {
        mutableStateOf(false)
    }

    if (usedPhoto) {
        val view = LocalView.current

        val window = remember(view) {
            (view.context as Activity).window
        }

        val insetsController = remember(view, window) {
            WindowCompat.getInsetsController(window, view)
        }

        SideEffect {
            insetsController.isAppearanceLightStatusBars =
                !isDarkTheme && overlappedFraction > 0.01f
        }

        DisposableEffect(Unit) {
            onDispose {
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CustomTopAppBar(
                titleText = when (dayPeriod) {
                    DayPeriod.MORNING -> stringResource(R.string.good_morning)
                    DayPeriod.DAY -> stringResource(R.string.good_afternoon)
                    DayPeriod.EVENING -> stringResource(R.string.good_evening)
                    DayPeriod.NIGHT -> stringResource(R.string.good_night)
                } + "!",
                subtitleText = namedScheduleState.reviewState?.let { reviewData ->
                    buildString {
                        when (reviewData.date) {
                            currentDate -> append(stringResource(R.string.today))
                            currentDate.plusDays(1) -> append(stringResource(R.string.tomorrow))
                            else -> reviewData.date.format(
                                dayMonthYearFormatter
                            )
                        }

                        append(" ")

                        if (reviewData.events.isEmpty()) {
                            append(stringResource(R.string.no_events).replaceFirstChar { it.lowercase() })
                        } else {
                            append(
                                "${reviewData.events.size} " + pluralStringResource(
                                    R.plurals.events,
                                    reviewData.events.size
                                )
                            )
                        }
                    }
                },
                scrollBehavior = if (usedPhoto) scrollBehavior else null,
                contentColor = contentColor
            ) {
                AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = !usedPhoto || showTopBarActions
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                appBackStack.openDialog(Route.Dialog.SearchDialog)
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                appBackStack.openDialog(Route.Dialog.AddScheduleDialog)
                            }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.add),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    ) { internalPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = if (usedPhoto) 0.dp else internalPadding.calculateTopPadding() + 12.dp,
                bottom = externalPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (usedPhoto) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = when (dayPeriod) {
                                DayPeriod.MORNING -> painterResource(R.drawable.day)
                                DayPeriod.DAY -> painterResource(R.drawable.day)
                                DayPeriod.EVENING -> painterResource(R.drawable.evening)
                                DayPeriod.NIGHT -> painterResource(R.drawable.night)
                            },
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .padding(
                                vertical = 8.dp, horizontal = 16.dp
                            )
                            .onGloballyPositioned { coordinates ->
                                val y = coordinates.positionInWindow().y

                                showTopBarActions = y <= topBarHeightPx
                            }
                    ) {
                        RowGroup(
                            items = listOf(
                                {
                                    ClickableItem(
                                        defaultMinHeight = 32.dp,
                                        showClickLabel = false,
                                        title = "${stringResource(R.string.find)} ${stringResource(R.string.schedule).replaceFirstChar { it.lowercase() }}",
                                        titleTypography = MaterialTheme.typography.titleSmall,
                                        leadingIcon = {
                                            LeadingIcon(
                                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                                iconSize = 20.dp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    ) {
                                        appBackStack.openDialog(Route.Dialog.SearchDialog)
                                    }
                                }, {
                                    ClickableItem(
                                        defaultMinHeight = 32.dp,
                                        showClickLabel = false,
                                        title = "${stringResource(R.string.create)} ${
                                            stringResource(
                                                R.string.schedule
                                            ).replaceFirstChar { it.lowercase() }
                                        }",
                                        titleTypography = MaterialTheme.typography.titleSmall,
                                        leadingIcon = {
                                            LeadingIcon(
                                                imageVector = ImageVector.vectorResource(R.drawable.add),
                                                iconSize = 20.dp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    ) {
                                        appBackStack.openDialog(Route.Dialog.AddScheduleDialog)
                                    }
                                }
                            )
                        )
                    }
                }
            }
            if (namedSchedules.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        ExpandedItem(
                            title = stringResource(R.string.saved_schedules),
                            visible = reviewUiState.visibleSavedSchedules,
                            onChangeVisibility = reviewUiState.onChangeVisibilitySavedSchedules
                        ) {
                            ColumnGroup(
                                items = namedSchedules.map { namedSchedule ->
                                    {
                                        ClickableItem(
                                            title = namedSchedule.shortName,
                                            titleMaxLines = 1,
                                            defaultMinHeight = 32.dp,
                                            leadingIcon = {
                                                Icon(
                                                    modifier = Modifier.size(16.dp),
                                                    imageVector = when (namedSchedule.type) {
                                                        NamedScheduleType.GROUP -> ImageVector.vectorResource(
                                                            R.drawable.group
                                                        )

                                                        NamedScheduleType.PERSON -> ImageVector.vectorResource(
                                                            R.drawable.person
                                                        )

                                                        NamedScheduleType.ROOM -> ImageVector.vectorResource(
                                                            R.drawable.room
                                                        )

                                                        NamedScheduleType.MY -> ImageVector.vectorResource(
                                                            R.drawable.edit
                                                        )
                                                    },
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            },
                                            trailingIcon = if (namedSchedule.isDefault) {
                                                {
                                                    Icon(
                                                        modifier = Modifier.size(20.dp),
                                                        imageVector = ImageVector.vectorResource(
                                                            R.drawable.check
                                                        ),
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            } else null
                                        ) {
                                            namedScheduleDialog = namedSchedule
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ExpandedItem(
                        title = stringResource(R.string.services),
                        visible = reviewUiState.visibleServices,
                        onChangeVisibility = reviewUiState.onChangeVisibilityServices
                    ) {
                        ColumnGroup(
                            items = listOf(
                                {
                                    ClickableItem(
                                        title = stringResource(R.string.news),
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier.size(16.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.news),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        },
                                    ) {
                                        appBackStack.openDialog(Route.Dialog.NewsList)
                                    }
                                }, {
                                    ClickableItem(
                                        title = stringResource(R.string.curriculum),
                                        leadingIcon = {
                                            Icon(
                                                modifier = Modifier.size(16.dp),
                                                imageVector = ImageVector.vectorResource(R.drawable.person),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        },
                                    ) {
                                        appBackStack.openDialog(Route.Dialog.CurriculumDialog)
                                    }
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    namedScheduleDialog?.let {
        ModalDialogNamedSchedule(
            namedSchedule = it,
            scheduleViewModel = scheduleViewModel,
            appBackStack = appBackStack,
            isSavedNamedSchedule = true,
            isDefaultNamedSchedule = it.isDefault,
            isDarkTheme = true,
            onOpenNamedSchedule = { namedScheduleId, setDefault, navigateToStart ->
                scheduleViewModel.setNamedSchedule(
                    namedScheduleId = namedScheduleId,
                    setDefault = setDefault
                )
                if (navigateToStart) appBackStack.navigateToStartRage()
            }
        ) {
            namedScheduleDialog = null
        }
    }

    deleteNamedScheduleDialog?.let {
        CustomAlertDialog(
            dialogIcon = ImageVector.vectorResource(R.drawable.delete),
            dialogTitle = "${stringResource(R.string.delete_schedule)}?",
            dialogText = stringResource(R.string.impossible_restore_eventextra),
            onDismissRequest = {
                deleteNamedScheduleDialog = null
            },
            onConfirmation = {
                scheduleViewModel.deleteNamedSchedule(
                    namedScheduleId = it.id,
                    isDefault = it.isDefault
                )
            }
        )
    }
}