package com.egormelnikoff.schedulerutmiit.core.ui.components.button_row

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerWidth
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme


@Composable
fun <T> AppButtonRow(
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
                        activeContainerColor = AppTheme.colors.primary,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = AppTheme.colors.onPrimary,
                        inactiveContainerColor = AppTheme.colors.secondaryContainer,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = AppTheme.colors.onSecondaryContainer
                    ),
                    border = BorderStroke(
                        width = 0.dp,
                        color = Color.Unspecified
                    ),
                    shape = index.appRowItemShape(
                        lastIndex = elements.lastIndex
                    ),
                    icon = {},
                    onClick = {
                        onClick(element)
                    },
                    selected = element == selectedElement,
                    label = {
                        label(index to element)
                    }
                )
                if (index != elements.lastIndex) {
                    SpacerWidth(AppTheme.paddings.padding2)
                }
            }
        }
    }
}

@Composable
fun Int.appRowItemShape(
    lastIndex: Int
): RoundedCornerShape {
    val isFirst = this == 0
    val isLast = this == lastIndex

    return RoundedCornerShape(
        topStart = if (isFirst) AppTheme.radius.radius12 else AppTheme.radius.radius2,
        topEnd = if (isLast) AppTheme.radius.radius12 else AppTheme.radius.radius2,
        bottomStart = if (isFirst) AppTheme.radius.radius12 else AppTheme.radius.radius2,
        bottomEnd = if (isLast) AppTheme.radius.radius12 else AppTheme.radius.radius2
    )
}

@Preview(showBackground = true)
@Composable
fun AppButtonRowPreview() {
    PreviewLightTheme {
        AppButtonRow(
            selectedElement = 1,
            elements = (1..2).toList(),
            onClick = {},
            label = {
                Text(
                    text = "Тест",
                    style = AppTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}