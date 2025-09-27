package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.AppSettings
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserRoutes.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.ui.elements.GroupItem
import kotlinx.coroutines.launch

data class Theme(
    val name: String,
    val imageVector: ImageVector,
    val displayedName: String
)

@Composable
fun SettingsScreen(
    onShowDialogInfo: () -> Unit,

    preferencesDataStore: DataStore,
    appSettings: AppSettings,

    settingsListState: LazyStaggeredGridState,
    paddingValues: PaddingValues,
    uriHandler: UriHandler
) {
    val scope = rememberCoroutineScope()


    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding()
        ),
        state = settingsListState
    ) {
        item {
            GroupItem(
                title = LocalContext.current.getString(R.string.schedule),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = {
                                scope.launch {
                                    preferencesDataStore.setViewEvent(!appSettings.eventView)
                                }
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.compact),
                            text = LocalContext.current.getString(R.string.compact_view)
                        ) {
                            CustomSwitch(
                                checked = appSettings.eventView,
                                onCheckedChange = {
                                    scope.launch {
                                        preferencesDataStore.setViewEvent(it)
                                    }
                                }
                            )
                        }
                    },
                    {
                        SettingsItem(
                            onClick = {
                                scope.launch {
                                    preferencesDataStore.setShowCountClasses(!appSettings.showCountClasses)
                                }
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.count),
                            text = LocalContext.current.getString(R.string.show_count_classes)
                        ) {
                            CustomSwitch(
                                checked = appSettings.showCountClasses,
                                onCheckedChange = {
                                    scope.launch {
                                        preferencesDataStore.setShowCountClasses(it)
                                    }
                                }
                            )
                        }
                    }
                )
            )
        }
        item {
            GroupItem(
                title = LocalContext.current.getString(R.string.decor),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = null,
                            imageVector = ImageVector.vectorResource(R.drawable.sun),
                            text = LocalContext.current.getString(R.string.theme),
                            horizontal = false
                        ) {
                            ThemeSelector(
                                preferences = preferencesDataStore,
                                currentTheme = appSettings.theme
                            )
                        }
                    },
                    {
                        SettingsItem(
                            onClick = null,
                            imageVector = ImageVector.vectorResource(R.drawable.color),
                            text = LocalContext.current.getString(R.string.color_style),
                            horizontal = false
                        ) {
                            ColorSelector(
                                currentSelected = appSettings.decorColorIndex,
                                onColorSelect = { value ->
                                    scope.launch {
                                        preferencesDataStore.setDecorColor(value)
                                    }
                                }
                            )
                        }
                    }
                )
            )
        }
        item {
            GroupItem(
                title = LocalContext.current.getString(R.string.general),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = {
                                uriHandler.openUri(APP_CHANNEL_URL)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.send),
                            text = LocalContext.current.getString(R.string.report_a_problem),
                        )
                    }, {
                        SettingsItem(
                            onClick = {
                                onShowDialogInfo()
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            text = LocalContext.current.getString(R.string.about_app),
                        )
                    }
                )
            )
        }
    }
}

@Composable
fun SettingsItem(
    onClick: (() -> Unit)?,
    imageVector: ImageVector,
    text: String,
    horizontal: Boolean = true,
    content: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .let {
                if (onClick != null) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            }

            .padding(horizontal = 16.dp, vertical = 16.dp)
            .defaultMinSize(minHeight = 36.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = imageVector,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            if (horizontal) {
                content?.invoke()
            }

        }
        if (!horizontal) {
            Spacer(modifier = Modifier.height(8.dp))
            content?.invoke()
        }
    }
}

@Composable
fun ThemeSelector(
    preferences: DataStore,
    currentTheme: String
) {
    val themes = arrayOf(
        Theme(
            name = "light",
            imageVector = ImageVector.vectorResource(R.drawable.sun),
            displayedName = LocalContext.current.getString(R.string.light)
        ),
        Theme(
            name = "dark",
            imageVector = ImageVector.vectorResource(R.drawable.moon),
            displayedName = LocalContext.current.getString(R.string.dark)
        ),
        Theme(
            name = "system",
            imageVector = ImageVector.vectorResource(R.drawable.error),
            displayedName = LocalContext.current.getString(R.string.auto)
        ),
    )

    val scope = rememberCoroutineScope()
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
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = themes.size,
                    baseShape = RoundedCornerShape(12.dp)
                ),
                onClick = {
                    scope.launch {
                        preferences.setTheme(theme.name)
                    }
                },
                selected = theme.name == currentTheme,
                icon = {},
                label = {
                    if (theme.name != "system") {
                        Icon(
                            modifier = Modifier
                                .size(16.dp),
                            imageVector = theme.imageVector,
                            contentDescription = theme.displayedName
                        )
                    } else {
                        Text(
                            text = theme.displayedName,
                            fontSize = 12.sp,
                        )
                    }
                }
            )
        }
    }
}