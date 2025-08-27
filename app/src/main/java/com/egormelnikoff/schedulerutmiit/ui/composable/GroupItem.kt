package com.egormelnikoff.schedulerutmiit.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GroupItem(
    title: String? = null,
    titleColor: Color? = null,
    items: List<@Composable () -> Unit>
) {
    Column(
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
                val edgeCorner = 16.dp
                val interiorCorner = 8.dp
                val shape = when {
                    isFirst && isLast -> RoundedCornerShape(edgeCorner)
                    isFirst -> RoundedCornerShape(
                        topStart = edgeCorner,
                        topEnd = edgeCorner,
                        bottomStart = interiorCorner,
                        bottomEnd = interiorCorner
                    )

                    isLast -> RoundedCornerShape(
                        topStart = interiorCorner,
                        topEnd = interiorCorner,
                        bottomStart = edgeCorner,
                        bottomEnd = edgeCorner
                    )

                    else -> RoundedCornerShape(interiorCorner)
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