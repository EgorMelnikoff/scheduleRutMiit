package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.ui.theme.baseItemSpacing
import com.egormelnikoff.schedulerutmiit.ui.theme.extraSmallCornerRadius
import com.egormelnikoff.schedulerutmiit.ui.theme.largeCornerRadius

@Composable
fun GridGroup(
    title: String? = null,
    titleColor: Color? = null,
    items: List<List<@Composable () -> Unit>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor ?: MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(baseItemSpacing)
        ) {
            items.forEachIndexed { indexColumn, itemDataColumn ->
                val isFirstColumn = indexColumn == 0
                val isLastColumn = indexColumn == items.lastIndex

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(baseItemSpacing)
                ) {
                    itemDataColumn.forEachIndexed { indexRow, itemDataRow ->
                        val isFirstRow = indexRow == 0
                        val isLastRow = indexRow == itemDataColumn.lastIndex

                        val topStartRadius =
                            if (isFirstColumn && isFirstRow) largeCornerRadius else extraSmallCornerRadius
                        val topEndRadius =
                            if (isFirstColumn && isLastRow) largeCornerRadius else extraSmallCornerRadius
                        val bottomStartRadius =
                            if (isLastColumn && isFirstRow) largeCornerRadius else extraSmallCornerRadius
                        val bottomEndRadius =
                            if (isLastColumn && isLastRow) largeCornerRadius else extraSmallCornerRadius

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
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            itemDataRow()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnGroup(
    title: String? = null,
    titleColor: Color? = null,
    withBackground: Boolean = true,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor ?: MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(baseItemSpacing)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .let {
                            if (withBackground) {
                                val topStartRadius = if (isFirst) largeCornerRadius else extraSmallCornerRadius
                                val topEndRadius = if (isFirst) largeCornerRadius else extraSmallCornerRadius
                                val bottomStartRadius = if (isLast) largeCornerRadius else extraSmallCornerRadius
                                val bottomEndRadius = if (isLast) largeCornerRadius else extraSmallCornerRadius

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
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
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

@Composable
fun RowGroup(
    title: String? = null,
    titleColor: Color? = null,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor ?: MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(baseItemSpacing)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                val topStartRadius = if (isFirst) largeCornerRadius else extraSmallCornerRadius
                val bottomStartRadius = if (isFirst) largeCornerRadius else extraSmallCornerRadius
                val topEndRadius = if (isLast) largeCornerRadius else extraSmallCornerRadius
                val bottomEndRadius = if (isLast) largeCornerRadius else extraSmallCornerRadius

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
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    itemData()
                }
            }
        }
    }
}