package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.AppShimmerCard
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.ShimmerBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
internal fun SearchLoadingWidget() {
    ShimmerBox(
        modifier = Modifier.padding(
            horizontal = AppTheme.paddings.padding8
        )
    ) { brush ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding20)
        ) {
            SearchBlockLoading(brush, 3)
            SearchBlockLoading(brush, 2)
        }
    }
}

@Composable
fun SearchBlockLoading(brush: Brush, count: Int) {
    Column {
        AppShimmerCard(
            brush = brush,
            width = 100.dp,
            height = 24.dp,
            shape = AppTheme.shapes.small
        )
        SpacerHeight(AppTheme.paddings.padding6)
        repeat(count) { index ->
            AppShimmerCard(
                brush = brush,
                height = 48.dp,
                shape = AppTheme.shapes.small
            )
            if (index != count - 1) {
                SpacerHeight(AppTheme.paddings.padding8)
            }
        }
    }
}