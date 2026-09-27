package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.AddScheduleAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_schedule.AddScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addNonPeriodicScheduleStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreenWidget(
    addScheduleState: AddScheduleState,
    onAction: (AddScheduleAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppTopBar(
                titleText = stringResource(R.string.create_schedule),
                navAction = {
                    onAction(AddScheduleAction.OnShowBackDialog(true))
                }
            )
        }
    ) { contentPadding ->
        ScaffoldLazyColumn(
            contentPadding = contentPadding,
            itemSpacing = 12.dp
        ) {
            item {
                AddScheduleInfoWidget(
                    focusManager = focusManager,
                    addScheduleState = addScheduleState,
                    onAction = onAction
                )
            }

            item {
                AddScheduleTypeWidget(
                    addScheduleState = addScheduleState,
                    onAction = onAction
                )
            }

            item {
                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    buttonTitle = stringResource(R.string.create),
                    enabled = addScheduleState.isButtonEnabled,
                    onClick = {
                        onAction(AddScheduleAction.OnSaveClicked)
                    }
                )
            }
        }
    }
}


@Preview(locale = "ru")
@Composable
fun AddScheduleScreenWidgetPreview() {
    PreviewLightTheme {
        AddScheduleScreenWidget(addNonPeriodicScheduleStateMock) { }
    }
}
