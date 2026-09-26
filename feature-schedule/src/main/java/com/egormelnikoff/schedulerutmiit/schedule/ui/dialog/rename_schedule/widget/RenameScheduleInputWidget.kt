package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.RenameScheduleAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.RenameScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.renameScheduleStateMock

@Composable
fun RenameScheduleInputWidget(
    renameScheduleState: RenameScheduleState.Loaded,
    onAction: (RenameScheduleAction) -> Unit
) {
    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        value = renameScheduleState.newName,
        maxSymbols = 50,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Default
        ),
        placeholderText = stringResource(R.string.name),
        trailingIcon = {
            AnimatedVisibility(
                visible = renameScheduleState.newName.isNotEmpty(),
                enter = scaleIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                IconButton(
                    onClick = {
                        onAction(RenameScheduleAction.OnNameChanged(""))
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.clear),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { name ->
        onAction(RenameScheduleAction.OnNameChanged(name))
    }
}


@Preview(locale = "ru", showBackground = true)
@Composable
fun RenameScheduleInputWidgetPreview() {
    PreviewLightTheme {
        RenameScheduleInputWidget(renameScheduleStateMock) { }
    }
}