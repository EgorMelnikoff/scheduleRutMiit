package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState

fun LazyListScope.eventTagWidget(
    eventScreenState: EventScreenState.Loaded,
    onAction: (EventScreenAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ColumnGroup(
                title = stringResource(R.string.tag),
                titleColor = MaterialTheme.colorScheme.primary,
                withBackground = false,
                items = listOf {
                    ColorSelector(
                        currentSelected = eventScreenState.tag,
                        onColorSelect = { tag ->
                            onAction(EventScreenAction.OnTagChanged(tag))
                        }
                    )
                }
            )
        }
    }
}