package com.egormelnikoff.schedulerutmiit.core.ui.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun GridGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color? = null,
    items: List<List<@Composable (Shape) -> Unit>>
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding4),
                text = it,
                style = AppTheme.typography.titleSmall,
                color = titleColor ?: AppTheme.colors.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding2)
        ) {
            items.forEachIndexed { indexColumn, itemDataColumn ->
                val isFirstColumn = indexColumn == 0
                val isLastColumn = indexColumn == items.lastIndex

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding2)
                ) {
                    itemDataColumn.forEachIndexed { indexRow, itemDataRow ->
                        val isFirstRow = indexRow == 0
                        val isLastRow = indexRow == itemDataColumn.lastIndex

                        val topStartRadius =
                            if (isFirstColumn && isFirstRow) AppTheme.radius.radius12 else AppTheme.radius.radius2
                        val topEndRadius =
                            if (isFirstColumn && isLastRow) AppTheme.radius.radius12 else AppTheme.radius.radius2
                        val bottomStartRadius =
                            if (isLastColumn && isFirstRow) AppTheme.radius.radius12 else AppTheme.radius.radius2
                        val bottomEndRadius =
                            if (isLastColumn && isLastRow) AppTheme.radius.radius12 else AppTheme.radius.radius2

                        val shape = remember(
                            topStartRadius,
                            topEndRadius,
                            bottomStartRadius,
                            bottomEndRadius
                        ) {
                            RoundedCornerShape(
                                topStart = topStartRadius,
                                topEnd = topEndRadius,
                                bottomStart = bottomStartRadius,
                                bottomEnd = bottomEndRadius
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(AppTheme.colors.secondaryContainer)
                        ) {
                            CompositionLocalProvider(
                                LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                            ) {
                                itemDataRow(shape)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color? = null,
    withBackground: Boolean = true,
    backgroundColor: Color = AppTheme.colors.secondaryContainer,
    items: List<@Composable () -> Unit>
) {
    if (items.isNotEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            title?.let {
                Text(
                    modifier = Modifier.padding(horizontal = AppTheme.paddings.padding4),
                    text = it,
                    style = AppTheme.typography.titleSmall,
                    color = titleColor ?: AppTheme.colors.onSecondaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding2)
            ) {
                items.forEachIndexed { index, itemData ->
                    val isFirst = index == 0
                    val isLast = index == items.lastIndex

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .let {
                                if (withBackground) {
                                    val topStartRadius = if (isFirst) AppTheme.radius.radius16 else AppTheme.radius.radius4
                                    val topEndRadius = if (isFirst) AppTheme.radius.radius16 else AppTheme.radius.radius4
                                    val bottomStartRadius = if (isLast) AppTheme.radius.radius16 else AppTheme.radius.radius4
                                    val bottomEndRadius = if (isLast) AppTheme.radius.radius16 else AppTheme.radius.radius4

                                    val shape = remember(
                                        topStartRadius,
                                        topEndRadius,
                                        bottomStartRadius,
                                        bottomEndRadius
                                    ) {
                                        RoundedCornerShape(
                                            topStart = topStartRadius,
                                            topEnd = topEndRadius,
                                            bottomStart = bottomStartRadius,
                                            bottomEnd = bottomEndRadius
                                        )
                                    }
                                    it
                                        .clip(shape)
                                        .background(backgroundColor)
                                } else {
                                    it
                                }
                            }

                    ) {
                        itemData()
                    }
                }
            }
        }
    }
}

@Composable
fun RowGroup(
    title: String? = null,
    titleColor: Color? = null,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = AppTheme.paddings.padding4),
                text = it,
                style = AppTheme.typography.titleSmall,
                color = titleColor ?: AppTheme.colors.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding2)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                val topStartRadius = if (isFirst) AppTheme.radius.radius16 else AppTheme.radius.radius4
                val bottomStartRadius = if (isFirst) AppTheme.radius.radius16 else AppTheme.radius.radius4
                val topEndRadius = if (isLast) AppTheme.radius.radius16 else AppTheme.radius.radius4
                val bottomEndRadius = if (isLast) AppTheme.radius.radius16 else AppTheme.radius.radius4

                val shape = remember(
                    topStartRadius,
                    topEndRadius,
                    bottomStartRadius,
                    bottomEndRadius
                ) {
                    RoundedCornerShape(
                        topStart = topStartRadius,
                        topEnd = topEndRadius,
                        bottomStart = bottomStartRadius,
                        bottomEnd = bottomEndRadius
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(AppTheme.colors.secondaryContainer)
                ) {
                    itemData()
                }
            }
        }
    }
}