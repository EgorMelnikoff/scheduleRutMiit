package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.RenameScheduleAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.RenameScheduleState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.renameScheduleStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameScheduleScreenWidget(
    renameScheduleState: RenameScheduleState,
    onAction: (RenameScheduleAction) -> Unit
) {
    when (renameScheduleState) {
        is RenameScheduleState.Loading -> LoadingScreen()

        is RenameScheduleState.Loaded -> {
            Scaffold(
                topBar = {
                    AppTopBar(
                        titleText = stringResource(R.string.renaming),
                        navAction = {
                            onAction(RenameScheduleAction.OnBackClicked)
                        }
                    )
                }
            ) { contentPadding ->
                ScaffoldLazyColumn(
                    contentPadding = contentPadding,
                    itemSpacing = 12.dp
                ) {
                    item {
                        RenameScheduleInputWidget(
                            renameScheduleState = renameScheduleState,
                            onAction = onAction
                        )
                    }

                    item {
                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            buttonTitle = stringResource(R.string.save),
                            enabled = renameScheduleState.renameEnabled,
                            onClick = {
                                onAction(RenameScheduleAction.OnSaveClicked)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(locale = "ru")
@Composable
fun RenameScheduleScreenWidgetPreview() {
    PreviewLightTheme {
        RenameScheduleScreenWidget(renameScheduleStateMock) { }
    }
}