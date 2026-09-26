package com.egormelnikoff.schedulerutmiit.core.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScaffoldLazyColumn(
    contentPadding: PaddingValues,
    background: Color = MaterialTheme.colorScheme.background,
    horizontalSpacing: Dp = 16.dp,
    itemSpacing: Dp = 8.dp,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding(),
            start = horizontalSpacing, end = horizontalSpacing
        ),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        content = content
    )
}