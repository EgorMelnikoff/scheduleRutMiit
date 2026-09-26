package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.RemoveButton
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.lecturerMock
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.roomMock

@Composable
fun RoomInputWidget(
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
            value = room.name,
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

@Preview(locale = "ru", showBackground = true)
@Composable
fun RoomInputWidgetPreview() {
    PreviewLightTheme {
        RoomInputWidget(roomMock, {}, {})
    }
}