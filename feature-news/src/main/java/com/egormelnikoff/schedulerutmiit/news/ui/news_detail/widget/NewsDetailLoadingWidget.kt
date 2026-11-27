package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.AppShimmerCard
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.ShimmerBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
internal fun NewsDetailLoadingWidget() {
    Scaffold { paddingValues ->
        ShimmerBox { brush ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding() + AppTheme.paddings.padding16,
                        start = AppTheme.paddings.padding16, end = AppTheme.paddings.padding16
                    ),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
            ) {
                item {
                    AppShimmerCard(
                        brush = brush,
                        height = 160.dp,
                        shape = AppTheme.shapes.small
                    )
                }

                item {
                    AppShimmerCard(
                        brush = brush,
                        width = 220.dp,
                        height = 24.dp,
                        shape = AppTheme.shapes.small
                    )
                }

                item {
                    AppShimmerCard(
                        brush = brush,
                        width = 100.dp,
                        height = 24.dp,
                        shape = AppTheme.shapes.small
                    )
                    SpacerHeight(20.dp)
                }


                paragraphShimmer(brush)
                paragraphShimmer(brush)
                paragraphShimmer(brush)
            }
        }
    }
}


internal fun LazyListScope.paragraphShimmer(brush: Brush) {
    item {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
        ) {
            AppShimmerCard(
                brush = brush,
                width = 300.dp,
                height = 20.dp,
                shape = AppTheme.shapes.small
            )
            AppShimmerCard(
                brush = brush,
                width = 220.dp,
                height = 20.dp,
                shape = AppTheme.shapes.small
            )
            AppShimmerCard(
                brush = brush,
                width = 270.dp,
                height = 20.dp,
                shape = AppTheme.shapes.small
            )
            AppShimmerCard(
                brush = brush,
                width = 180.dp,
                height = 20.dp,
                shape = AppTheme.shapes.small
            )
            AppShimmerCard(
                brush = brush,
                width = 220.dp,
                height = 20.dp,
                shape = AppTheme.shapes.small
            )
            SpacerHeight(AppTheme.paddings.padding12)
        }
    }
}

@Preview
@Composable
fun NewsDetailLoadingWidgetPreview() {
    PreviewLightTheme {
        NewsDetailLoadingWidget()
    }
}