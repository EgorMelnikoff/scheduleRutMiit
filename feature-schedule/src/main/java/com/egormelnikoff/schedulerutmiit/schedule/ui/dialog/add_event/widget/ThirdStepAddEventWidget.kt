package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ListInput
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addEventStateMock

@Composable
fun ThirdStepAddEventWidget(
    focusManager: FocusManager,
    state: AddEventState.Loaded,
    onAction: (AddEventAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        ListInput(
            title = stringResource(R.string.room),
            elements = state.editForm.rooms,
            onAddElement = {
                onAction(AddEventAction.OnAddRoom)
                focusManager.clearFocus()
            },
            maxCount = 1
        ) { index, room ->
            RoomInputWidget(
                room = room,
                onValueChanged = { updatedRoom ->
                    onAction(AddEventAction.OnEditRoom(index, updatedRoom))
                },
                onRemove = {
                    onAction(AddEventAction.OnRemoveRoom(index))
                }
            )
        }
        ListInput(
            title = stringResource(R.string.lecturers),
            elements = state.editForm.lecturers,
            onAddElement = {
                onAction(AddEventAction.OnAddLecturer)
                focusManager.clearFocus()
            },
            maxCount = 3
        ) { index, lecturer ->
            LecturerInputWidget(
                lecturer = lecturer,
                onValueChanged = { updatedLecturer ->
                    onAction(
                        AddEventAction.OnEditLecturer(
                            index,
                            updatedLecturer
                        )
                    )
                },
                onRemove = {
                    onAction(AddEventAction.OnRemoveLecturer(index))
                }
            )
        }
        ListInput(
            title = stringResource(R.string.groups),
            elements = state.editForm.groups,
            onAddElement = {
                onAction(AddEventAction.OnAddGroup)
                focusManager.clearFocus()
            },
            maxCount = 7
        ) { index, group ->
            GroupInputWidget(
                group = group,
                onValueChanged = { updatedGroup ->
                    onAction(
                        AddEventAction.OnEditGroup(
                            index, updatedGroup
                        )
                    )
                },
                onRemove = {
                    onAction(AddEventAction.OnRemoveGroup(index))
                }
            )
        }
    }
}

@Preview(locale = "ru", showSystemUi = false, showBackground = true)
@Composable
fun ThirdStepAddEventWidgetPreview() {
    PreviewLightTheme {
        ThirdStepAddEventWidget(LocalFocusManager.current, addEventStateMock) { }
    }
}