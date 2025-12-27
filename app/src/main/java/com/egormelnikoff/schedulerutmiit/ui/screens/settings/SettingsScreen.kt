package com.egormelnikoff.schedulerutmiit.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.AppConst.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.data.datasource.local.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.state.AppUiState
import com.egormelnikoff.schedulerutmiit.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

data class ThemeSelectorItemContent(
    val name: String,
    val imageVector: ImageVector?,
    val displayedName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appUiState: AppUiState,
    appSettings: AppSettings,
    navigationActions: NavigationActions,
    settingsViewModel: SettingsViewModel,
    externalPadding: PaddingValues
) {
    var eventViewDialog by remember { mutableStateOf(false) }

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
        state = appUiState.settingsListState
    ) {
        item {
            ColumnGroup(
                title = stringResource(R.string.schedule),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = {
                                eventViewDialog = true
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.compact),
                            text = stringResource(R.string.compact_view)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.right),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                CustomSwitch(
                                    checked = (!appSettings.eventView.groupsVisible && !appSettings.eventView.roomsVisible && !appSettings.eventView.lecturersVisible && !appSettings.eventView.tagVisible && !appSettings.eventView.commentVisible)
                                ) {
                                    settingsViewModel.onSetEventView(!it)
                                }
                            }
                        }
                    },
                    {
                        SettingsItem(
                            onClick = {
                                settingsViewModel.onSetShowCountClasses(!appSettings.showCountClasses)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.count),
                            text = stringResource(R.string.show_count_classes)
                        ) {
                            CustomSwitch(
                                checked = appSettings.showCountClasses
                            ) {
                                settingsViewModel.onSetShowCountClasses(it)
                            }
                        }
                    }
                )
            )
        }
        item {
            ColumnGroup(
                title = stringResource(R.string.decor),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = null,
                            imageVector = ImageVector.vectorResource(R.drawable.sun),
                            text = stringResource(R.string.theme),
                            horizontal = false
                        ) {
                            ThemeSelector(
                                setTheme = { value ->
                                    settingsViewModel.onSetTheme(value)
                                },
                                currentTheme = appSettings.theme
                            )
                        }
                    },
                    {
                        SettingsItem(
                            onClick = null,
                            imageVector = ImageVector.vectorResource(R.drawable.color),
                            text = stringResource(R.string.color_style),
                            horizontal = false
                        ) {
                            ColorSelector(
                                currentSelected = appSettings.decorColorIndex
                            ) { value ->
                                settingsViewModel.onSetDecorColor(value)
                            }
                        }
                    }
                )
            )
        }
        item {
            ColumnGroup(
                title = stringResource(R.string.general),
                items = listOf(
                    {
                        SettingsItem(
                            onClick = {
                                appUiState.uriHandler.openUri(APP_CHANNEL_URL)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.send),
                            text = stringResource(R.string.report_a_problem),
                        )
                    }, {
                        SettingsItem(
                            onClick = {
                                settingsViewModel.sendLogsFile()
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.bug_report),
                            text = stringResource(R.string.send_logs_by_email),
                        )
                    }, {
                        SettingsItem(
                            onClick = {
                                navigationActions.navigateToInfoDialog()
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            text = stringResource(R.string.about_app),
                        )
                    }
                )
            )
        }
    }
    StatusBarProtection()

    if (eventViewDialog) {
        CustomModalBottomSheet(
            modifier = Modifier.padding(horizontal = 8.dp),
            onDismiss = {
                eventViewDialog = false
            }
        ) {
            CheckedItem(
                text = stringResource(R.string.groups),
                imageVector = ImageVector.vectorResource(R.drawable.group),
                checked = appSettings.eventView.groupsVisible
            ) { visible ->
                settingsViewModel.onSetEventGroupVisibility(visible)
            }
            CheckedItem(
                text = stringResource(R.string.rooms),
                imageVector = ImageVector.vectorResource(R.drawable.room),
                checked = appSettings.eventView.roomsVisible
            ) { visible ->
                settingsViewModel.onSetEventRoomsVisibility(visible)
            }
            CheckedItem(
                text = stringResource(R.string.lecturers),
                imageVector = ImageVector.vectorResource(R.drawable.person),
                checked = appSettings.eventView.lecturersVisible
            ) { visible ->
                settingsViewModel.onSetEventLecturersVisibility(visible)
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline
            )
            CheckedItem(
                text = stringResource(R.string.tag),
                imageVector = ImageVector.vectorResource(R.drawable.tag),
                checked = appSettings.eventView.tagVisible
            ) { visible ->
                settingsViewModel.onSetEventTagVisibility(visible)
            }
            CheckedItem(
                text = stringResource(R.string.comment),
                imageVector = ImageVector.vectorResource(R.drawable.comment),
                checked = appSettings.eventView.commentVisible
            ) { visible ->
                settingsViewModel.onSetEventCommentVisibility(visible)
            }
        }
    }
}