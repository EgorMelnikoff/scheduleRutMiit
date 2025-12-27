package com.egormelnikoff.schedulerutmiit.ui.dialogs.add_event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Lecturer
import com.egormelnikoff.schedulerutmiit.app.model.Room
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.RemoveButton

@Composable
fun LecturerInput(
    lecturer: Lecturer,
    onValueChanged: (Lecturer) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            value = lecturer.shortFio ?: "",
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = stringResource(R.string.full_name_of_lecturer),
        ) { newValue ->
            onValueChanged(lecturer.copy(shortFio = newValue, fullFio = newValue))
        }
        RemoveButton { onRemove() }
    }
}


@Composable
fun RoomInput(
    room: Room,
    onValueChanged: (Room) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            value = room.name ?: "",
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = stringResource(R.string.room_number),
        ) { newValue ->
            onValueChanged(room.copy(name = newValue, hint = newValue))
        }
        RemoveButton { onRemove() }
    }
}

@Composable
fun GroupInput(
    group: Group,
    onValueChanged: (Group) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            value = group.name ?: "",
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = stringResource(R.string.group_number),
        ) { newValue ->
            onValueChanged(group.copy(name = newValue))
        }
        RemoveButton { onRemove() }
    }
}