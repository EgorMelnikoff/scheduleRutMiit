package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.collections.lastIndex

@Composable
fun <T> CustomButtonRow(
    colors: SegmentedButtonColors? = null,
    selectedElement: T,
    elements: List<T>,
    onClick: (T) -> Unit,
    label: @Composable (Pair<Int, T>) -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
            space = 0.dp
        ) {
            elements.forEachIndexed { index, element ->
                SegmentedButton(
                    colors = colors ?: SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    border = BorderStroke(
                        width = 0.dp,
                        color = Color.Unspecified
                    ),
                    shape = index.customRowItemShape(
                        lastIndex = elements.lastIndex
                    ),
                    icon = {},
                    onClick = {
                        onClick(element)
                    },
                    selected = element == selectedElement,
                    label = {
                        label(Pair(index, element))
                    }
                )
                if (index != elements.lastIndex) {
                    Spacer(
                        modifier = Modifier.width(3.dp)
                    )
                }
            }
        }
    }
}

fun Int.customRowItemShape(
    lastIndex: Int
): RoundedCornerShape {
    val isFirst = this == 0
    val isLast = this == lastIndex

    return RoundedCornerShape(
        topStart = if (isFirst) edgeCorner else interiorCorner,
        topEnd = if (isLast) edgeCorner else interiorCorner,
        bottomStart = if (isFirst) edgeCorner else interiorCorner,
        bottomEnd = if (isLast) edgeCorner else interiorCorner
    )
}