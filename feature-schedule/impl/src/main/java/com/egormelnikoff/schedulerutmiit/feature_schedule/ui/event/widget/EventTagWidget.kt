package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ColorSelectorWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState

fun LazyListScope.eventTagWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit
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
                    ColorSelectorWidget(
                        currentSelected = eventState.tag,
                        onColorSelect = { tag ->
                            onAction(EventAction.OnTagChanged(tag))
                        }
                    )
                }
            )
        }
    }
}