package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.hidden_events.HiddenEventsAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.eventMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenEventsScreenWidget(
    hiddenEvents: List<Event>,
    onAction: (HiddenEventsAction) -> Unit
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
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