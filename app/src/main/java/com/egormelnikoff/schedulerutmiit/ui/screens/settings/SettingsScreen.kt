package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.AppConst.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.ui.theme.StatusBarProtection

data class ThemeSelectorItemContent(
    val name: String,
    val imageVector: ImageVector,
    val displayedName: String
)

@Composable
fun SettingsScreen(
    externalPadding: PaddingValues,
    onShowDialogInfo: () -> Unit,
    onSendLogs: () -> Unit,
    onOpenUri: (String) -> Unit,
    onSetViewEvent: (Boolean) -> Unit,
    onSetShowCountClasses: (Boolean) -> Unit,
    onSetTheme: (String) -> Unit,
    onSetDecorColor: (Int) -> Unit,
    appSettings: AppSettings,
    settingsListState: LazyStaggeredGridState
) {
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
            top = externalPadding.calculateTopPadding() + 16.dp,
            bottom = externalPadding.calculateBottomPadding()
        ),
        state = settingsListState
    ) {
        item {
            ColumnGroup(
                title = LocalContext.current.getString(R.string.schedule),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = {
                                onSetViewEvent(!appSettings.eventView)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.compact),
                            text = LocalContext.current.getString(R.string.compact_view)
                        ) {
                            CustomSwitch(
                                checked = appSettings.eventView,
                                onCheckedChange = {
                                    onSetViewEvent(it)
                                }
                            )
                        }
                    },
                    {
                        SettingsItem(
                            onClick = {
                                onSetShowCountClasses(!appSettings.showCountClasses)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.count),
                            text = LocalContext.current.getString(R.string.show_count_classes)
                        ) {
                            CustomSwitch(
                                checked = appSettings.showCountClasses,
                                onCheckedChange = {
                                    onSetShowCountClasses(it)
                                }
                            )
                        }
                    }
                )
            )
        }
        item {
            ColumnGroup(
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
                                setTheme = onSetTheme,
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
                                    onSetDecorColor(value)
                                }
                            )
                        }
                    }
                )
            )
        }
        item {
            ColumnGroup(
                title = LocalContext.current.getString(R.string.general),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = {
                                onOpenUri(APP_CHANNEL_URL)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.send),
                            text = LocalContext.current.getString(R.string.report_a_problem),
                        )
                    }, {
                        SettingsItem(
                            onClick = {
                                onSendLogs()
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.bug_report),
                            text = LocalContext.current.getString(R.string.send_logs_by_email),
                        )
                    },{
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
    StatusBarProtection()
}

@Composable
fun SettingsItem(
    onClick: (() -> Unit)?,
    text: String,
    imageVector: ImageVector,
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
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
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
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            if (horizontal) {
                content?.invoke()
            }
        }
        if (!horizontal) {
            content?.invoke()
        }
    }
}

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
            imageVector = ImageVector.vectorResource(R.drawable.error),
            displayedName = LocalContext.current.getString(R.string.auto)
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
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                )
            }
        }
    }
}