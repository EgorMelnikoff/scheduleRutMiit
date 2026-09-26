package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.eventStateMock

@Composable
fun EventScreenWidget(
    eventScreenState: EventScreenState,
    onAction: (EventScreenAction) -> Unit,
) {
    when (eventScreenState) {
        is EventScreenState.Loading -> LoadingScreen()
        is EventScreenState.Loaded -> {
            Scaffold(
                modifier = Modifier.imePadding(),
                topBar = {
                    EventScreenHeaderWidget(
                        eventScreenState = eventScreenState,
                        onAction = onAction
                    )
                }
            ) { contentPadding ->
                ScaffoldLazyColumn(
                    contentPadding = contentPadding,
                    horizontalSpacing = 0.dp,
                    itemSpacing = 12.dp
                ) {
                    item {
                        EventNameWidget(
                            eventScreenState.schedule.timetableType, eventScreenState.event, 16.dp
                        )
                    }
                    eventGroupsWidget(eventScreenState, onAction)

                    eventRoomsWidget(eventScreenState)

                    eventLecturersWidget(eventScreenState, onAction)

                    eventCommentWidget(eventScreenState, onAction)

                    eventTagWidget(eventScreenState, onAction)
                }
            }
        }
    }
}


@Preview(locale = "ru")
@Composable
fun EventScreenWidgetPreview() {
    PreviewLightTheme{
        EventScreenWidget(eventStateMock) { }
    }
}