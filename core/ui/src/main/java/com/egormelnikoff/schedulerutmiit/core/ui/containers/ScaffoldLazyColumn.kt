package com.egormelnikoff.schedulerutmiit.core.ui.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun ScaffoldLazyColumn(
    contentPadding: PaddingValues = PaddingValues(),
    background: Color = AppTheme.colors.background,
    horizontalSpacing: Dp = 16.dp,
    itemSpacing: Dp = 8.dp,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + AppTheme.paddings.padding12,
            bottom = contentPadding.calculateBottomPadding(),
            start = horizontalSpacing, end = horizontalSpacing
        ),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        content = content
    )
}