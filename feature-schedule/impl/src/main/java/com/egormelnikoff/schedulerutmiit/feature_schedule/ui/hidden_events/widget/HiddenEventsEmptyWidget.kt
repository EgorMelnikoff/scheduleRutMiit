package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.hidden_events.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen

fun LazyListScope.hiddenEventsEmptyWidget(
    contentPadding: PaddingValues
) {
    item {
        EmptyScreen(
            modifier = Modifier.fillParentMaxSize(),
            title = "¯\\_(ツ)_/¯",
            subtitle = stringResource(R.string.empty_here),
            isBoldTitle = false,
            paddingBottom = contentPadding.calculateBottomPadding()
        )
    }
}