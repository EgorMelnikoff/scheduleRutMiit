package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow

@Composable
fun EventsCountSelector(
    setEventsCountView: (EventsCountView) -> Unit,
    currentView: EventsCountView
) {
    CustomButtonRow(
        selectedElement = currentView,
        elements = EventsCountView.entries,
        colors = SegmentedButtonDefaults.colors().copy(
            activeContainerColor = MaterialTheme.colorScheme.primary,
            activeBorderColor = Color.Transparent,
            activeContentColor = MaterialTheme.colorScheme.onPrimary,
            inactiveContainerColor = MaterialTheme.colorScheme.background,
            inactiveBorderColor = Color.Transparent,
            inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        onClick = { themeName ->
            setEventsCountView(themeName)
        }
    ) { eventsCountView ->
        Text(
            text = when (eventsCountView.second) {
                EventsCountView.DETAILS -> stringResource(R.string.details)
                EventsCountView.BRIEFLY -> stringResource(R.string.briefly)
                EventsCountView.OFF -> stringResource(R.string.off)
            },
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}