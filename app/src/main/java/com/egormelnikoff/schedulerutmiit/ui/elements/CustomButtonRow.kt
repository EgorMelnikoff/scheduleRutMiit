package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> CustomButtonRow(
    selectedElement: T,
    elements: List<T>,
    onClick: (T) -> Unit,
    label: @Composable (Pair<Int, T>) -> Unit
){
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            elements.forEachIndexed { index, element ->
                SegmentedButton(
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = elements.size,
                        baseShape = MaterialTheme.shapes.medium
                    ),
                    onClick = {
                        onClick(element)
                    },
                    selected = element == selectedElement,
                    label = {
                        label(Pair(index, element))
                    }
                )
            }
        }
    }
}