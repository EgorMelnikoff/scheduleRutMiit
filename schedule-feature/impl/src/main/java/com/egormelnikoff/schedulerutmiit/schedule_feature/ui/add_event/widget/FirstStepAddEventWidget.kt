package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.add_event.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.chip.AppFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RoundedBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.DefaultEventParams
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.add_event.AddEventAction
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.addEventStateMock

@Composable
fun FirstStepAddEventWidget(
    focusManager: FocusManager,
    state: AddEventState.Loaded,
    onAction: (AddEventAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.editForm.name,
            placeholderText = stringResource(R.string.class_name),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done
            )
        ) { name ->
            onAction(AddEventAction.OnNameChanged(name))
        }
        RoundedBox(
            backgroundColor = Color.Unspecified,
            title = stringResource(R.string.class_type),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterVertically
                )
            ) {
                DefaultEventParams.types.forEach { type ->
                    AppFilterChip(
                        title = type
                            ?: stringResource(R.string.not_specified),
                        imageVector = null,
                        selected = type == state.editForm.type,
                        onClick = {
                            onAction(AddEventAction.OnTypeChanged(type))
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}


@Preview(
    locale = "ru", showBackground = true
)
@Composable
fun FirstStepAddEventWidgetPreview() {
    PreviewLightTheme {
        FirstStepAddEventWidget(LocalFocusManager.current, addEventStateMock) { }
    }
}