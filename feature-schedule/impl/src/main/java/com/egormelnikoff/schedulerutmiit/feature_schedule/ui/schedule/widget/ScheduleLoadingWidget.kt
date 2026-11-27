package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerWidth
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.AppShimmerCard
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.ShimmerBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme

@Composable
internal fun SchedulePeriodicLoadingWidget() {
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
                verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16)
            ) {
                item {
                    ScheduleShimmerHeaderCard(brush)
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppShimmerCard(
                            brush = brush,
                            width = 20.dp,
                            height = 20.dp,
                            shape = AppTheme.shapes.small
                        )
                        SpacerWidth(AppTheme.paddings.padding12)
                        AppShimmerCard(
                            brush = brush,
                            width = 200.dp,
                            height = 20.dp,
                            shape = AppTheme.shapes.small
                        )
                        SpacerWidth(AppTheme.paddings.padding12)
                        AppShimmerCard(
                            brush = brush,
                            width = 20.dp,
                            height = 20.dp,
                            shape = AppTheme.shapes.small
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        repeat(7) { index ->
                            AppShimmerCard(
                                brush = brush,
                                width = 36.dp,
                                height = 50.dp,
                                shape = AppTheme.shapes.small
                            )
                            if (index != 6) {
                                SpacerWidth(AppTheme.paddings.padding4)
                            }
                        }
                    }
                }
                items(4) {
                    EventShimmerCard(brush)
                }
            }
        }
    }
}

@Composable
internal fun ScheduleNonPeriodicLoadingWidget() {
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
                verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16)
            ) {
                item {
                    ScheduleShimmerHeaderCard(brush)
                }

                item {
                    AppShimmerCard(
                        brush = brush,
                        width = 120.dp,
                        height = 24.dp,
                        shape = AppTheme.shapes.small
                    )
                }

                items(2) {
                    EventShimmerCard(brush)
                }

                item {
                    AppShimmerCard(
                        brush = brush,
                        width = 120.dp,
                        height = 24.dp,
                        shape = AppTheme.shapes.small
                    )
                }

                item {
                    EventShimmerCard(brush)
                }

                item {
                    AppShimmerCard(
                        brush = brush,
                        width = 120.dp,
                        height = 24.dp,
                        shape = AppTheme.shapes.small
                    )
                }

                items(3) {
                    EventShimmerCard(brush)
                }
            }
        }
    }
}

@Composable
internal fun ScheduleShimmerHeaderCard(brush: Brush) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
    ) {
        AppShimmerCard(
            brush = brush,
            width = 100.dp,
            height = 20.dp,
            shape = AppTheme.shapes.small
        )

        AppShimmerCard(
            brush = brush,
            width = 80.dp,
            height = 20.dp,
            shape = AppTheme.shapes.small
        )
        SpacerHeight(AppTheme.paddings.padding8)
    }
}

@Composable
internal fun EventShimmerCard(brush: Brush) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding12)
        ){
            AppShimmerCard(
                brush = brush,
                width = 50.dp,
                height = 16.dp,
                shape = AppTheme.shapes.small
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            AppShimmerCard(
                brush = brush,
                width = 100.dp,
                height = 16.dp,
                shape = AppTheme.shapes.small
            )
        }
        AppShimmerCard(
            brush = brush,
            height = 120.dp
        )
    }
}

@Preview
@Composable
fun SchedulePeriodicLoadingWidgetPreview() {
    PreviewLightTheme {
        SchedulePeriodicLoadingWidget()
    }
}

@Preview
@Composable
fun ScheduleNonPeriodicLoadingWidgetPreview() {
    PreviewLightTheme {
        ScheduleNonPeriodicLoadingWidget()
    }
}