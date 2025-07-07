package com.egormelnikoff.schedulerutmiit.ui.settings

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SchedulesState
import com.egormelnikoff.schedulerutmiit.DataStore
import com.egormelnikoff.schedulerutmiit.data.repos.remote.parser.ParserRoutes.APP_CHANNEL_URL
import com.egormelnikoff.schedulerutmiit.ui.theme.LightGrey
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeGreen
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeLightBlue
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeOrange
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemePink
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeRed
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeViolet
import com.egormelnikoff.schedulerutmiit.ui.theme.darkThemeYellow
import com.egormelnikoff.schedulerutmiit.ui.view_models.AppInfoState
import com.egormelnikoff.schedulerutmiit.ui.view_models.SettingsViewModel
import kotlinx.coroutines.launch

data class Theme(
    val name: String,
    val imageVector: ImageVector,
    val displayedName: String
)

@Composable
fun SettingsTopBar(
    title: String,
    navAction: (() -> Unit)? = null,
    navImageVector: ImageVector,
    actions: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navAction != null) {
            IconButton(
                onClick = navAction
            ) {
                Icon(
                    imageVector = navImageVector,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color =  MaterialTheme.colorScheme.onBackground
        )
        actions?.invoke()
    }
}

@Composable
fun SettingsScreen(
    navigateToSearch: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    settingsViewModel: SettingsViewModel,
    preferencesDataStore: DataStore,
    appSettings: AppSettings,

    schedulesState: SchedulesState,
    appInfoState: AppInfoState,
    showDialogSchedules: Boolean,
    onShowDialogSchedules: (Boolean) -> Unit,
    showDialogInfo: Boolean,
    onShowDialogInfo: (Boolean) -> Unit,
    //showDialogAddSchedule: Boolean,
    //onShowDialogAddSchedule: (Boolean) -> Unit,
    //onShowDialogAddEvent: (Long?) -> Unit,
    settingsListState: ScrollState,
    paddingValues: PaddingValues,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
    val switchColors = SwitchDefaults.colors().copy(
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        uncheckedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.surface
    )

    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()),
        targetState = Pair(showDialogSchedules, showDialogInfo),
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { target ->
        when {
            target.first -> {
                BackHandler {
                    onShowDialogSchedules(false)
                }
                SchedulesDialog(
                    onShowDialog = onShowDialogSchedules,
                    navigateToSearch = navigateToSearch,
                    scheduleViewModel = scheduleViewModel,
                    schedulesState = schedulesState,
                    //showDialogAddSchedule = onShowDialogSddSchedule,
                    //onShowDialogAddEvent = onShowDialogAddEvent,
                    paddingBottom = paddingValues.calculateBottomPadding()
                )

            }


            target.second -> {
                BackHandler {
                    onShowDialogInfo(false)
                }
                InfoDialog(
                    appInfoState = appInfoState,
                    onShowDialog = onShowDialogInfo,
                    paddingBottom = paddingValues.calculateBottomPadding()
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(settingsListState)
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    GroupSettingsItem(
                        title = LocalContext.current.getString(R.string.schedule)
                    ) {
                        if (schedulesState is SchedulesState.Loaded) {
                            SettingsItem(
                                onClick = {
                                    onShowDialogSchedules(!showDialogSchedules)
                                },
                                imageVector = ImageVector.vectorResource(R.drawable.schedule),
                                text = LocalContext.current.getString(R.string.schedules)
                            ) {
                                Badge(
                                    containerColor = Color.Unspecified,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ) {
                                    Text(
                                        text = schedulesState.savedSchedules.size.toString(),
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outline,
                                thickness = 0.5.dp
                            )
                        }
                        SettingsItem(
                            onClick = {
                                scope.launch {
                                    preferencesDataStore.setViewEvent(!appSettings.eventView)
                                }
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.compact),
                            text = LocalContext.current.getString(R.string.compact_view)
                        ) {
                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                                Switch(
                                    checked = appSettings.eventView,
                                    onCheckedChange = {
                                        scope.launch {
                                            preferencesDataStore.setViewEvent(it)
                                        }
                                    },
                                    colors = switchColors
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp
                        )
                        SettingsItem(
                            onClick = {
                                scope.launch {
                                    preferencesDataStore.setShowCountClasses(!appSettings.showCountClasses)
                                }
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.resource_class),
                            text = LocalContext.current.getString(R.string.show_count_classes)
                        ) {
                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                                Switch(
                                    modifier = Modifier.padding(0.dp),
                                    checked = appSettings.showCountClasses,
                                    onCheckedChange = {
                                        scope.launch {
                                            preferencesDataStore.setShowCountClasses(it)
                                        }
                                    },
                                    colors = switchColors
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GroupSettingsItem(
                        title = LocalContext.current.getString(R.string.general)
                    ) {
                        SettingsItem(
                            onClick = null,
                            imageVector = ImageVector.vectorResource(R.drawable.sun),
                            text = LocalContext.current.getString(R.string.theme),
                            horizontal = false
                        ) {
                            ThemeSelector(
                                preferences = preferencesDataStore,
                                currentTheme = appSettings.theme,
                                themes = themes
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp
                        )
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
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp
                        )
                        SettingsItem(
                            onClick = {
                                val intent =
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        APP_CHANNEL_URL.toUri()
                                    )
                                context.startActivity(intent)
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.send),
                            text = LocalContext.current.getString(R.string.report_a_problem)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp
                        )
                        SettingsItem(
                            onClick = {
                                onShowDialogInfo(true)
                                if (appInfoState !is AppInfoState.Loaded) {
                                    settingsViewModel.getInfo()
                                }
                            },
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            text = LocalContext.current.getString(R.string.about_app)
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(paddingValues.calculateBottomPadding())
                    )
                }
            }
        }
    }
}

@Composable
fun GroupSettingsItem(
    title: String? = null,
    content: @Composable (() -> Unit),
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)

        ) {
            content.invoke()
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
            .fillMaxWidth()
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

@Composable
fun ThemeSelector(
    preferences: DataStore,
    currentTheme: String,
    themes: Array<Theme>,
) {
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