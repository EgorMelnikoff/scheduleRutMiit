package com.egormelnikoff.schedulerutmiit.core.settings.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.settings.R
import com.egormelnikoff.schedulerutmiit.core.settings.api.model.ThemePreferences
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components._switch.AppSwitch
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Custom
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ColorSelectorWidget
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ThemeSelectorWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeModalDialog(
    themePreferences: ThemePreferences,
    onAction: (SettingsAction) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(
        isDarkTheme = themePreferences.theme.isDarkTheme(),
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
        showHandle = false,
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.settings_theme),
            items = listOf(
                {
                    Box(
                        modifier = Modifier.padding(AppTheme.paddings.padding12)
                    ) {
                        ThemeSelectorWidget(
                            setTheme = { value ->
                                onAction(SettingsAction.OnSetTheme(value))
                            },
                            currentTheme = themePreferences.theme
                        )
                    }
                }, {
                    AppCell(
                        center = {
                            TextBlock(
                                text = stringResource(R.string.settings_use_amoled),
                            )
                        },
                        end = {
                            Custom {
                                AppSwitch(
                                    checked = themePreferences.usedAmoled,
                                    enabled = themePreferences.theme.isDarkTheme(),
                                ) { used ->
                                    onAction(SettingsAction.OnSetUsedAmoled(used))
                                }
                            }
                        }
                    )
                }
            )
        )
        SpacerHeight(0.dp)

        ColumnGroup(
            title = stringResource(R.string.settings_color_style),
            items = listOf {
                ColorSelectorWidget(
                    currentSelected = themePreferences.decorColorIndex
                ) { value ->
                    onAction(SettingsAction.OnSetDecorColor(value))
                }

            }
        )
    }
}

