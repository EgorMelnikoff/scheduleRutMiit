package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.egormelnikoff.schedulerutmiit.ui.theme.Blue
import com.egormelnikoff.schedulerutmiit.ui.theme.Green
import com.egormelnikoff.schedulerutmiit.ui.theme.LightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.LightGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.Orange
import com.egormelnikoff.schedulerutmiit.ui.theme.Pink
import com.egormelnikoff.schedulerutmiit.ui.theme.Red
import com.egormelnikoff.schedulerutmiit.ui.theme.Violet
import com.egormelnikoff.schedulerutmiit.ui.theme.Yellow

@Composable
fun ColorSelector(
    currentSelected: Int,
    onColorSelect: (Int) -> Unit,
) {
    val colors = arrayOf(
        LightGrey,
        Red,
        Orange,
        Yellow,
        Green,
        LightBlue,
        Blue,
        Violet,
        Pink,
    )
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            colors.forEachIndexed { index, color ->
                SegmentedButton(
                    border = BorderStroke(width = 0.dp, Color.Transparent),
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = color,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = color,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = Color.Transparent
                    ),
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = colors.size,
                        baseShape = MaterialTheme.shapes.medium
                    ),
                    onClick = {
                        onColorSelect(index)
                    },
                    selected = index == currentSelected,
                    icon = {},
                    label = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.check),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}