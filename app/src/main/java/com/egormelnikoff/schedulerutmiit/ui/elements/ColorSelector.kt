package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.egormelnikoff.schedulerutmiit.ui.theme.LightGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeYellow

@Composable
fun ColorSelector(
    currentSelected: Int,
    onColorSelect: (Int) -> Unit,
) {
    val colors = arrayOf(
        LightGrey,
        darkThemeRed,
        darkThemeOrange,
        darkThemeYellow,
        darkThemeGreen,
        darkThemeLightBlue,
        darkThemeBlue,
        darkThemeViolet,
        darkThemePink,
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
                        baseShape = RoundedCornerShape(12.dp)
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