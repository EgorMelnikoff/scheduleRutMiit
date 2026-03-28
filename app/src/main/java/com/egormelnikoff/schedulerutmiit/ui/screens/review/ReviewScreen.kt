package com.egormelnikoff.schedulerutmiit.ui.screens.review

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums.DayPeriod
import com.egormelnikoff.schedulerutmiit.app.extension.dayPeriod
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.ExpandedItem
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.ui.elements.ModalDialogNamedSchedule
import com.egormelnikoff.schedulerutmiit.ui.elements.RowGroup
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleState
import com.egormelnikoff.schedulerutmiit.view_models.schedule.ScheduleViewModel
import java.time.LocalDateTime

@Composable
fun ReviewScreen(
    scheduleState: ScheduleState,
    reviewUiState: ReviewUiState,
    currentDateTime: LocalDateTime,
    scheduleViewModel: ScheduleViewModel,
    appBackStack: AppBackStack,
    isDarkTheme: Boolean,
    externalPadding: PaddingValues
) {
    val spacerHeight = 270.dp
    val currentDate = currentDateTime.toLocalDate()
    val dayPeriod = currentDateTime.dayPeriod()

    val view = LocalView.current
    val window = (view.context as Activity).window
    val insetsController = remember(view, window) {
        WindowCompat.getInsetsController(window, view)
    }

    SideEffect {
        insetsController.isAppearanceLightStatusBars = false
    }

    DisposableEffect(Unit) {
        onDispose {
            val activity = view.context as? Activity
            if (activity?.isChangingConfigurations == false) {
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
            }
        }
    }

    var showNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }
    var showDeleteNamedScheduleDialog by remember { mutableStateOf<NamedScheduleEntity?>(null) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacerHeight + 20.dp),
                    painter = when (dayPeriod) {
                        DayPeriod.MORNING -> painterResource(R.drawable.day)
                        DayPeriod.DAY -> painterResource(R.drawable.day)
                        DayPeriod.EVENING -> painterResource(R.drawable.evening)
                        DayPeriod.NIGHT -> painterResource(R.drawable.night)
                    },
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier
                        .padding(
                            top = paddingValues.calculateTopPadding() + 8.dp,
                            start = 16.dp, end = 16.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = when (dayPeriod) {
                            DayPeriod.MORNING -> "Доброе утро!"
                            DayPeriod.DAY -> "Добрый день!"
                            DayPeriod.EVENING -> "Добрый вечер!"
                            DayPeriod.NIGHT -> "Доброй ночи!"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    scheduleState.defaultNamedScheduleData?.scheduleData?.reviewData?.let { reviewData ->
                        when (reviewData.displayedDate) {
                            currentDate -> {
                                Text(
                                    text = stringResource(R.string.today) +
                                            " ${reviewData.events.size} " +
                                            pluralStringResource(
                                                R.plurals.events,
                                                reviewData.events.size
                                            ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }

                            currentDate.plusDays(1) -> {
                                Text(
                                    text = stringResource(R.string.tomorrow) +
                                            " ${reviewData.events.size} " +
                                            pluralStringResource(
                                                R.plurals.events,
                                                reviewData.events.size
                                            ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )

                            }
                        }

                    }
                }
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(spacerHeight))
                Column(
                    modifier = Modifier
                        .dropShadow(
                            shape = MaterialTheme.shapes.extraLarge.copy(
                                bottomStart = CornerSize(0), bottomEnd = CornerSize(0)
                            ),
                            shadow = Shadow(
                                radius = 10.dp,
                                spread = 6.dp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                                offset = DpOffset(x = 2.dp, 2.dp)
                            )
                        )
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = MaterialTheme.shapes.extraLarge.copy(
                                bottomStart = CornerSize(0), bottomEnd = CornerSize(0)
                            )
                        )
                        .padding(
                            top = 16.dp, start = 16.dp, end = 16.dp,
                            bottom = externalPadding.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    title = "${stringResource(R.string.create)} ${stringResource(R.string.schedule).replaceFirstChar { it.lowercase() }}",
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
                    Spacer(modifier = Modifier.height(2.dp))
                    if (scheduleState.savedNamedSchedules.isNotEmpty()) {
                        ExpandedItem(
                            title = stringResource(R.string.saved_schedules),
                            visible = reviewUiState.visibleSavedSchedules,
                            onChangeVisibility = reviewUiState.onChangeVisibilitySavedSchedules
                        ) {
                            ColumnGroup(
                                items = scheduleState.savedNamedSchedules.map { namedScheduleEntity ->
                                    {
                                        ClickableItem(
                                            title = namedScheduleEntity.shortName,
                                            titleMaxLines = 1,
                                            defaultMinHeight = 32.dp,
                                            onClick = {
                                                showNamedScheduleDialog = namedScheduleEntity
                                            },
                                            trailingIcon = if (namedScheduleEntity.isDefault) {
                                                {
                                                    Icon(
                                                        modifier = Modifier.size(20.dp),
                                                        imageVector = ImageVector.vectorResource(R.drawable.check),
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            } else null
                                        )
                                    }
                                }
                            )
                        }
                    }
                    ExpandedItem(
                        title = stringResource(R.string.services),
                        visible = reviewUiState.visibleServices,
                        onChangeVisibility = reviewUiState.onChangeVisibilityServices
                    ) {
                        ColumnGroup(
                            items = listOf {
                                ClickableItem(
                                    title = stringResource(R.string.list_teachers)
                                ) {
                                    appBackStack.openDialog(Route.Dialog.CurriculumDialog)
                                }
                            }
                        )
                    }
                }
            }
        }


        showNamedScheduleDialog?.let {
            ModalDialogNamedSchedule(
                namedScheduleEntity = it,
                scheduleViewModel = scheduleViewModel,
                appBackStack = appBackStack,

                isSavedNamedSchedule = true,
                isDefaultNamedSchedule = it.isDefault,

                onOpenNamedSchedule = {
                    scheduleViewModel.getSavedNamedSchedule(
                        namedScheduleId = it.id
                    )
                    appBackStack.navigateToStartRage()
                }
            ) {
                showNamedScheduleDialog = null
            }
        }

        showDeleteNamedScheduleDialog?.let {
            CustomAlertDialog(
                dialogIcon = ImageVector.vectorResource(R.drawable.delete),
                dialogTitle = "${stringResource(R.string.delete_schedule)}?",
                dialogText = stringResource(R.string.impossible_restore_eventextra),
                onDismissRequest = {
                    showDeleteNamedScheduleDialog = null
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
}