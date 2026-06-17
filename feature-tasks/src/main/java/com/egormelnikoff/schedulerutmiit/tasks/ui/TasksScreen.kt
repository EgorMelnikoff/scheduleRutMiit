package com.egormelnikoff.schedulerutmiit.tasks.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomBadge
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomCheckBox
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.RoundedBox
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.Calendar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.CalendarBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.state.CalendarState
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Green
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Red
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.Yellow
import com.egormelnikoff.schedulerutmiit.core.ui.theme.color.getColorByIndex
import com.egormelnikoff.schedulerutmiit.tasks.domain.use_case.TaskAction
import com.egormelnikoff.schedulerutmiit.tasks.ui.view_model.TaskViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    appBackStack: AppBackStack,
    taskViewModel: TaskViewModel,
    calendarState: CalendarState,
    tasks: Map<LocalDate, List<Task>>,
    today: LocalDate,
    externalPadding: PaddingValues

) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.tasks)
            ) {
                IconButton(
                    onClick = {
                        appBackStack.openDialog(Route.Dialog.AddTaskDialog)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) { internalPadding ->
        Calendar(
            modifier = Modifier.padding(
                top = internalPadding.calculateTopPadding()
            ),
            calendarState = calendarState,
            showCalendarDialog = false,
            onShowCalendarDialog = { },
            showMonth = true,
            calendarBarItem = { _, currentDate ->
                val tasksPerDate = remember(currentDate, tasks) {
                    tasks[currentDate]
                }

                CalendarBarItem(
                    currentDate = currentDate,

                    isSelected = calendarState.selectedDate == currentDate,
                    isDisabled = currentDate !in calendarState.calendarData.startDate..calendarState.calendarData.endDate,
                    isToday = (currentDate == today),
                    selectDate = { date ->
                        calendarState.selectDate(date, tasksPerDate?.isEmpty() ?: true)
                    }
                ) {
                    tasksPerDate?.let {
                        CustomBadge(
                            count = tasksPerDate.size
                        )
                    }
                }
            }
        ) { _, currentDate ->
            val tasksPerDate = tasks[currentDate]?.sortedBy { it.time }

            val progressFloat by remember(tasksPerDate) {
                derivedStateOf {
                    val list = tasksPerDate ?: emptyList()
                    if (list.isNotEmpty()) {
                        val completed = list.count { it.isCompleted }
                        completed.toFloat() / list.size
                    } else {
                        0f
                    }
                }
            }

            val progressAnimated by animateFloatAsState(
                targetValue = progressFloat,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "ProgressAnimation"
            )

            val indicatorColor by remember(progressFloat) {
                derivedStateOf {
                    when {
                        progressFloat >= 1.0f -> Green
                        progressFloat >= 0.5f -> Yellow
                        else -> Red
                    }
                }
            }

            val indicatorColorAnimated by animateColorAsState(
                targetValue = indicatorColor,
                animationSpec = tween(durationMillis = 300),
                label = "ColorAnimation"
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = externalPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tasksPerDate != null) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            progress = { progressAnimated },
                            drawStopIndicator = {},
                            color = indicatorColorAnimated,
                            trackColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    items(tasksPerDate, key = { it.id }) { task ->
                        SingleTask(
                            task = task,
                            onComplete = { newValue ->
                                taskViewModel.taskAction(
                                    TaskAction.UpdateIsCompleted(
                                        task.id,
                                        task.date,
                                        newValue
                                    )
                                )
                            },
                            onOpen = { task ->
                                appBackStack.openDialog(
                                    Route.Dialog.EditTaskDialog(task)
                                )
                            },
                            onDelete = { id, date ->
                                taskViewModel.taskAction(
                                    TaskAction.DeleteByDateAndId(id, date)
                                )
                            },
                            onDeleteAll = { id ->
                                taskViewModel.taskAction(
                                    TaskAction.DeleteById(id)
                                )
                            }
                        )
                    }
                } else {
                    item {
                        Empty(
                            modifier = Modifier.fillParentMaxSize(),
                            title = "Заданий нет",
                            subtitle = "На этот день нет заданий"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleTask(
    onComplete: (Boolean) -> Unit,
    onDelete: (Long, LocalDate) -> Unit,
    onDeleteAll: (Long) -> Unit,
    onOpen: (Task) -> Unit,
    task: Task
) {
    var showExpandedMenu by remember { mutableStateOf(false) }

    RoundedBox {
        ClickableItem(
            title = task.text,
            titleDecoration = if (task.isCompleted) {
                TextDecoration.LineThrough
            } else null,
            subtitle = task.date.format(DateTimeFormatters.dayMonthYearFormatter)
                    + ", " + task.time.format(DateTimeFormatters.hourMinuteFormatter),
            trailingIcon = {
                CustomCheckBox(
                    checked = task.isCompleted,
                    onCheckedChange = { newValue ->
                        onComplete(newValue)
                    }
                )
            },
            leadingIcon = if (task.tag != 0) {
                {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.circle),
                        contentDescription = null,
                        tint = task.tag.getColorByIndex()
                    )
                }
            } else null,
            showClickLabel = false,
            onLongClick = {
                showExpandedMenu = !showExpandedMenu
            }
        ) {
            onOpen(task)
        }
        DropdownMenu(
            containerColor = MaterialTheme.colorScheme.background,
            expanded = showExpandedMenu,
            shape = MaterialTheme.shapes.medium,
            onDismissRequest = { showExpandedMenu = false }
        ) {
            DropdownMenuItem(
                colors = MenuDefaults.itemColors().copy(
                    textColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error
                ),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.delete)
                    )
                },
                onClick = {
                    onDelete(task.id, task.date)
                }
            )
            DropdownMenuItem(
                colors = MenuDefaults.itemColors().copy(
                    textColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error
                ),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete_all),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = "Удалить все"
                    )
                },
                onClick = {
                    onDeleteAll(task.id)
                }
            )
        }
    }
}