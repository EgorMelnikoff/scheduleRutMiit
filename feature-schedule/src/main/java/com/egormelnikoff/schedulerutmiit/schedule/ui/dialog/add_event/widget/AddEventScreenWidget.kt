package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcDateTime
import com.egormelnikoff.schedulerutmiit.core.ui.elements.AppTimePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.PagerScreenContainer
import com.egormelnikoff.schedulerutmiit.core.ui.elements.calendar.bottom_sheet.BottomSheetDatePicker
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addEventStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreenWidget(
    state: AddEventState,
    onAction: (AddEventAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = if (state is AddEventState.Loaded && state.editForm.isEdit) stringResource(R.string.editing)
                else stringResource(R.string.create_class),
                navAction = {
                    onAction(AddEventAction.OnShowBackDialog(true))
                }
            )
        }
    ) { contentPadding ->
        when (state) {
            is AddEventState.Loaded -> {
                val pagerState = rememberPagerState(
                    initialPage = 0
                ) { 3 }

                PagerScreenContainer(
                    pagerState = pagerState,
                    isNextEnabled = { page ->
                        when (page) {
                            0 -> state.editForm.name.isNotBlank()
                            1 -> state.editForm.date != null && state.editForm.startTime != null && state.editForm.endTime != null && (state.schedule.recurrence != null && state.editForm.interval != -1 || state.schedule.recurrence == null)
                            2 -> state.editForm.isValid
                            else -> true
                        }
                    },
                    onFinish = {
                        state.editForm.date?.let {
                            val startDateTime =
                                state.editForm.startTime?.toUtcDateTime(it)
                            val endDateTime =
                                state.editForm.endTime?.toUtcDateTime(it)
                            if (startDateTime != null && endDateTime != null) {
                                onAction(AddEventAction.OnSaveClicked)
                            }
                        }
                        onAction(AddEventAction.OnBackClicked)
                    },
                    paddingValues = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = contentPadding.calculateTopPadding() + 12.dp,
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    finishTitle = if (state.editForm.isEdit) {
                        stringResource(R.string.save)
                    } else {
                        stringResource(R.string.create)
                    }
                ) { page ->
                    when (page) {
                        0 -> FirstStepAddEventWidget(
                            focusManager = focusManager,
                            state = state,
                            onAction = onAction
                        )

                        1 -> SecondStepAddEventWidget(
                            focusManager = focusManager,
                            state = state,
                            onAction = onAction
                        )

                        2 -> ThirdStepAddEventWidget(
                            focusManager = focusManager,
                            state = state,
                            onAction = onAction
                        )
                    }
                }
            }

            is AddEventState.Loading -> LoadingScreen()
        }
    }
}


@Preview(locale = "ru")
@Composable
fun EventScreenWidgetPreview() {
    PreviewLightTheme {
        AddEventScreenWidget(addEventStateMock) { }
    }
}