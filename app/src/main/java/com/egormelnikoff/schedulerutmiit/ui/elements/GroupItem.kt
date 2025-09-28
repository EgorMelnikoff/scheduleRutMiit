package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GridGroup(
    title: String? = null,
    titleColor: Color? = null,
    edgeCorner: Dp = 16.dp,
    interiorCorner: Dp = 4.dp,
    items: List<List<@Composable () -> Unit>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEachIndexed { indexColumn, itemDataColumn ->
                val isFirstColumn = indexColumn == 0
                val isLastColumn = indexColumn == items.lastIndex

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    itemDataColumn.forEachIndexed { indexRow, itemDataRow ->
                        val isFirstRow = indexRow == 0
                        val isLastRow = indexRow == itemDataColumn.lastIndex

                        val topStartRadius = if (isFirstColumn && isFirstRow) edgeCorner else interiorCorner
                        val topEndRadius = if (isFirstColumn && isLastRow) edgeCorner else interiorCorner
                        val bottomStartRadius = if (isLastColumn && isFirstRow) edgeCorner else interiorCorner
                        val bottomEndRadius = if (isLastColumn && isLastRow) edgeCorner else interiorCorner

                        val shape = remember(topStartRadius, topEndRadius, bottomStartRadius, bottomEndRadius) {
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
                                .background(MaterialTheme.colorScheme.surface)
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
    edgeCorner: Dp = 16.dp,
    interiorCorner: Dp = 4.dp,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                val topStartRadius = if (isFirst) edgeCorner else interiorCorner
                val topEndRadius = if (isFirst) edgeCorner else interiorCorner
                val bottomStartRadius = if (isLast) edgeCorner else interiorCorner
                val bottomEndRadius = if (isLast) edgeCorner else interiorCorner

                val shape = remember(topStartRadius, topEndRadius, bottomStartRadius, bottomEndRadius) {
                    RoundedCornerShape(
                        topStart = topStartRadius,
                        topEnd = topEndRadius,
                        bottomStart = bottomStartRadius,
                        bottomEnd = bottomEndRadius
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)

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
    edgeCorner: Dp = 16.dp,
    interiorCorner: Dp = 4.dp,
    items: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items.forEachIndexed { index, itemData ->
                val isFirst = index == 0
                val isLast = index == items.lastIndex

                val topStartRadius = if (isFirst) edgeCorner else interiorCorner
                val topEndRadius = if (isFirst) edgeCorner else interiorCorner
                val bottomStartRadius = if (isLast) edgeCorner else interiorCorner
                val bottomEndRadius = if (isLast) edgeCorner else interiorCorner

                val shape = remember(topStartRadius, topEndRadius, bottomStartRadius, bottomEndRadius) {
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
                        .background(MaterialTheme.colorScheme.surface)

                ) {
                    itemData()
                }
            }
        }
    }
}