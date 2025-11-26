package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R

@Composable
fun ThemeSelector(
    setTheme: (String) -> Unit,
    currentTheme: String
) {
    val themes = arrayOf(
        ThemeSelectorItemContent(
            name = "light",
            imageVector = ImageVector.vectorResource(R.drawable.sun),
            displayedName = LocalContext.current.getString(R.string.light)
        ),
        ThemeSelectorItemContent(
            name = "dark",
            imageVector = ImageVector.vectorResource(R.drawable.moon),
            displayedName = LocalContext.current.getString(R.string.dark)
        ),
        ThemeSelectorItemContent(
            name = "system",
            imageVector = null,
            displayedName = LocalContext.current.getString(R.string.auto)
        ),
        ThemeSelectorItemContent(
            name = "amoled",
            imageVector = null,
            displayedName = LocalContext.current.getString(R.string.amoled)
        ),
    )

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            themes.forEachIndexed { index, theme ->
                SegmentedButton(
                    modifier = Modifier.fillMaxWidth(),
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeBorderColor = Color.Transparent,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.background,
                        inactiveBorderColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = themes.size,
                        baseShape = MaterialTheme.shapes.medium
                    ),
                    onClick = {
                        setTheme(theme.name)
                    },
                    selected = theme.name == currentTheme,
                    icon = {},
                    label = {
                        if (theme.name != "system" && theme.imageVector != null) {
                            Icon(
                                modifier = Modifier
                                    .size(16.dp),
                                imageVector = theme.imageVector,
                                contentDescription = theme.displayedName
                            )
                        } else {
                            Text(
                                text = theme.displayedName,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                )
            }
        }
    }
}