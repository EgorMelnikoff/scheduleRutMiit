package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.theme.color.colors

@Composable
fun ColorSelector(
    currentSelected: Int,
    onColorSelect: (Int) -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
            space = 0.dp
        ) {
            colors.entries.forEachIndexed { index, color ->
                SegmentedButton(
                    border = BorderStroke(width = 0.dp, Color.Unspecified),
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = color.value,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = color.value,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = Color.Transparent
                    ),
                    shape = index.customRowItemShape(
                        lastIndex = colors.size - 1
                    ),
                    onClick = {
                        onColorSelect(color.key)
                    },
                    selected = color.key == currentSelected,
                    icon = {},
                    label = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.check),
                            contentDescription = null
                        )
                    }
                )
                if (index != colors.size - 1) {
                    Spacer(
                        modifier = Modifier.width(3.dp)
                    )
                }
            }
        }
    }
}