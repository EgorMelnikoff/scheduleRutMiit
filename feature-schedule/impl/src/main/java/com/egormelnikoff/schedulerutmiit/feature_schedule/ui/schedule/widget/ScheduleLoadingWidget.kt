package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.AppShimmerCard
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun ScheduleLoadingWidget() {
    Scaffold { paddingValues ->
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    AppShimmerCard(
                        width = 100.dp,
                        height = 20.dp,
                        shape = AppTheme.shapes.small
                    )

                    AppShimmerCard(
                        width = 80.dp,
                        height = 20.dp,
                        shape = AppTheme.shapes.small
                    )
                    SpacerHeight(AppTheme.paddings.padding16)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            item {
                AppShimmerCard(
                    width = 100.dp,
                    height = 20.dp,
                    shape = AppTheme.shapes.small
                )
            }
            items(2) {
                AppShimmerCard(
                    height = 120.dp
                )
            }
            item {
                AppShimmerCard(
                    width = 100.dp,
                    height = 20.dp,
                    shape = AppTheme.shapes.small
                )
            }
            items(4) {
                AppShimmerCard(
                    height = 120.dp
                )
            }
            item {
                AppShimmerCard(
                    width = 100.dp,
                    height = 20.dp,
                    shape = AppTheme.shapes.small
                )
            }
            items(1) {
                AppShimmerCard(
                    height = 120.dp
                )
            }
        }
    }
}
