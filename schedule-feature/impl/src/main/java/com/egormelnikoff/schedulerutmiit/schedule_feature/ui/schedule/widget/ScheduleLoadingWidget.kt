package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.shimmer.AppShimmerCard

@Composable
fun ScheduleLoadingWidget() {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    start = 16.dp, end = 16.dp
                ),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppShimmerCard(
                        width = 100.dp,
                        height = 20.dp,
                        shape = MaterialTheme.shapes.small
                    )

                    AppShimmerCard(
                        width = 80.dp,
                        height = 20.dp,
                        shape = MaterialTheme.shapes.small
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            item {
                AppShimmerCard(
                    width = 100.dp,
                    height = 20.dp,
                    shape = MaterialTheme.shapes.small
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
                    shape = MaterialTheme.shapes.small
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
                    shape = MaterialTheme.shapes.small
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
