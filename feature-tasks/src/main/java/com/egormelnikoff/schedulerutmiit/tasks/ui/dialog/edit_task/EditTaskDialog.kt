package com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.BottomSheetTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingIcon
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.EditTaskViewModel
import com.egormelnikoff.schedulerutmiit.tasks.ui.dialog.edit_task.view_model.state.EditTaskState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    editTaskDialog: Route.Dialog.EditTaskDialog,
    onBack: () -> Unit
) {
    val editTaskViewModel = hiltViewModel<EditTaskViewModel, EditTaskViewModel.Factory> { factory ->
        factory.create(editTaskDialog.taskId, editTaskDialog.date)
    }

    when (val editTaskState = editTaskViewModel.editTaskState.collectAsStateWithLifecycle().value) {
        is EditTaskState.Loading -> LoadingScreen()
        is EditTaskState.Loaded -> {
            var showDialogTime by remember { mutableStateOf(false) }

            val focusManager = LocalFocusManager.current

            Scaffold(
                topBar = {
                    CustomTopAppBar(
                        titleText = stringResource(R.string.task), navAction = onBack
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 12.dp,
                            bottom = innerPadding.calculateBottomPadding() + 8.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = editTaskState.editTaskForm.text,
                            placeholderText = stringResource(R.string.class_name),
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false, imeAction = ImeAction.Done
                            )
                        ) { newValue ->
                            editTaskViewModel.updateText(newValue)
                        }

                        ColumnGroup(
                            items = listOf {
                                ClickableItem(
                                    defaultMinHeight = 32.dp,
                                    showClickLabel = false,
                                    title = editTaskState.editTaskForm.time.format(
                                        DateTimeFormatters.hourMinuteFormatter
                                    ),
                                    titleTypography = MaterialTheme.typography.titleSmall,
                                    leadingIcon = {
                                        LeadingIcon(
                                            imageVector = ImageVector.vectorResource(R.drawable.time),
                                            iconSize = 20.dp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                ) {
                                    focusManager.clearFocus()
                                    showDialogTime = true
                                }
                            }
                        )

                        ColumnGroup(
                            title = stringResource(R.string.tag),
                            titleColor = MaterialTheme.colorScheme.primary,
                            withBackground = false,
                            items = listOf {
                                ColorSelector(
                                    currentSelected = editTaskState.editTaskForm.tag,
                                    onColorSelect = { newTag ->
                                        editTaskViewModel.updateTag(newTag)
                                    }
                                )
                            }
                        )
                    }

                    CustomButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        buttonTitle = stringResource(R.string.save),
                        enabled = editTaskState.isButtonEnabled,
                        onClick = {
                            editTaskViewModel.saveChanges()
                        }
                    )
                }
            }

            if (showDialogTime) {
                BottomSheetTimePicker(
                    selectedTime = editTaskState.editTaskForm.time,
                    onTimeSelect = { newValue ->
                        editTaskViewModel.updateTime(newValue)
                    },
                    onShowDialog = { newValue -> showDialogTime = newValue },
                )
            }
        }
    }
}
