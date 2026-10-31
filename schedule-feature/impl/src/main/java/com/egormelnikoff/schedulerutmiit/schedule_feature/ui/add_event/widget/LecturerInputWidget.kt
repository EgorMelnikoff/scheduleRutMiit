package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.add_event.widget

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
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.lecturerMock

@Composable
fun LecturerInputWidget(
    lecturer: Lecturer,
    onValueChanged: (Lecturer) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            modifier = Modifier.weight(1f),
            value = lecturer.shortFio,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            placeholderText = stringResource(R.string.full_name_of_lecturer),
        ) { newValue ->
            onValueChanged(lecturer.copy(shortFio = newValue, fullFio = newValue))
        }
        RemoveButtonWidget { onRemove() }
    }
}

@Preview(locale = "ru", showBackground = true)
@Composable
fun LecturerInputWidgetPreview() {
    PreviewLightTheme {
        LecturerInputWidget(lecturerMock, {}, {})
    }
}