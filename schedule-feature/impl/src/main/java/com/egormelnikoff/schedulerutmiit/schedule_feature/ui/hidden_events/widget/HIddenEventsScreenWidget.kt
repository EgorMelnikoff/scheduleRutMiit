package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.hidden_events.widget

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.hidden_events.HiddenEventsAction
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.eventMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenEventsScreenWidget(
    hiddenEvents: List<Event>,
    onAction: (HiddenEventsAction) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                titleText = stringResource(R.string.hidden_events),
                navAction = {
                    onAction(HiddenEventsAction.OnBackClicked)
                }
            )
        }
    ) { contentPadding ->
        ScaffoldLazyColumn(
            contentPadding = contentPadding
        ) {
            if (hiddenEvents.isNotEmpty()) {
                hiddenEventsContentWidget(hiddenEvents, onAction)
            } else {
                hiddenEventsEmptyWidget(contentPadding)
            }
        }
    }
}

@Preview(locale = "ru")
@Composable
fun HiddenEventsScreenWidgetPreview() {
    PreviewLightTheme {
        HiddenEventsScreenWidget(listOf(eventMock, eventMock)) { }
    }
}

@Preview(locale = "ru")
@Composable
fun HiddenEventsScreenEmptyWidgetPreview() {
    PreviewLightTheme {
        HiddenEventsScreenWidget(listOf()) { }
    }
}