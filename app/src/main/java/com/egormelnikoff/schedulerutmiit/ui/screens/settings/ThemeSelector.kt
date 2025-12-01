package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButtonRow

@Composable
fun ThemeSelector(
    setTheme: (String) -> Unit,
    currentTheme: String
) {
    val themes = mapOf(
        0 to ThemeSelectorItemContent(
            name = "light",
            imageVector = ImageVector.vectorResource(R.drawable.sun),
            displayedName = LocalContext.current.getString(R.string.light)
        ),
        1 to ThemeSelectorItemContent(
            name = "dark",
            imageVector = ImageVector.vectorResource(R.drawable.moon),
            displayedName = LocalContext.current.getString(R.string.dark)
        ),
        2 to ThemeSelectorItemContent(
            name = "amoled",
            imageVector = null,
            displayedName = LocalContext.current.getString(R.string.amoled)
        ),
        3 to ThemeSelectorItemContent(
            name = "system",
            imageVector = null,
            displayedName = LocalContext.current.getString(R.string.auto)
        )
    )
    CustomButtonRow(
        selectedElement = currentTheme,
        elements = themes.entries.map { it.value.name },
        colors = SegmentedButtonDefaults.colors().copy(
            activeContainerColor = MaterialTheme.colorScheme.primary,
            activeBorderColor = Color.Transparent,
            activeContentColor = MaterialTheme.colorScheme.onPrimary,
            inactiveContainerColor = MaterialTheme.colorScheme.background,
            inactiveBorderColor = Color.Transparent,
            inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        onClick = { themeName ->
            setTheme(themeName)
        }
    ) { theme ->
        val currentTheme = themes[theme.first]!!
        if (currentTheme.imageVector != null) {
            Icon(
                modifier = Modifier
                    .size(16.dp),
                imageVector = currentTheme.imageVector,
                contentDescription = currentTheme.displayedName
            )
        } else {
            Text(
                text = currentTheme.displayedName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}