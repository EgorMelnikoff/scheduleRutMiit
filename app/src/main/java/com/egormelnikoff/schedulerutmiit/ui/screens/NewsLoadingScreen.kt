package com.egormelnikoff.schedulerutmiit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NewsLoadingScreen() {
    Scaffold { paddingValues ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            items(20) {
                LoadingCard(
                    height = 200.dp
                )
            }
        }
    }
}