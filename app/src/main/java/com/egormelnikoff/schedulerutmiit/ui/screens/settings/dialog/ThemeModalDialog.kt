package com.egormelnikoff.schedulerutmiit.ui.screens.settings.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.ui.screens.settings.selector.ThemeSelector
import com.egormelnikoff.schedulerutmiit.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.view_models.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel
) {
    CustomModalBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.theme),
            items = listOf(
                {
                    Box(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        ThemeSelector(
                            setTheme = { value ->
                                settingsViewModel.onSetTheme(value)
                            },
                            currentTheme = appSettings.theme
                        )
                    }
                }, {
                    ClickableItem(
                        title = stringResource(R.string.use_amoled),
                        defaultMinHeight = 36.dp,
                        trailingIcon = {
                            CustomSwitch(
                                checked = appSettings.usedAmoled,
                                enabled = appSettings.theme.isDarkTheme(),
                            ) { used ->
                                settingsViewModel.onSetUsedAmoled(used)
                            }
                        }
                    )
                }
            )
        )
        Spacer(modifier = Modifier.height(0.dp))
        ColumnGroup(
            title = stringResource(R.string.color_style),
            items = listOf {
                ColorSelector(
                    currentSelected = appSettings.decorColorIndex
                ) { value ->
                    settingsViewModel.onSetDecorColor(value)
                }

            }
        )
    }
}

