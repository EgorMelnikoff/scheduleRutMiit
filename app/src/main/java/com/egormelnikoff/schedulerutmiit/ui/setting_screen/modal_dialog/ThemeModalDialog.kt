package com.egormelnikoff.schedulerutmiit.ui.setting_screen.modal_dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.components._switch.AppSwitch
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ColorSelectorWidget
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ThemeSelectorWidget
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    preferencesViewModel: PreferencesViewModel
) {
    AppBottomSheet(
        isDarkTheme = appSettings.themePreferences.theme.isDarkTheme(),
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
                        ThemeSelectorWidget(
                            setTheme = { value ->
                                preferencesViewModel.onSetTheme(value)
                            },
                            currentTheme = appSettings.themePreferences.theme
                        )
                    }
                }, {
                    AppCell(
                        title = stringResource(R.string.use_amoled),
                        defaultMinHeight = 36.dp,
                        trailingIcon = {
                            AppSwitch(
                                checked = appSettings.themePreferences.usedAmoled,
                                enabled = appSettings.themePreferences.theme.isDarkTheme(),
                            ) { used ->
                                preferencesViewModel.onSetUsedAmoled(used)
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
                ColorSelectorWidget(
                    currentSelected = appSettings.themePreferences.decorColorIndex
                ) { value ->
                    preferencesViewModel.onSetDecorColor(value)
                }

            }
        )
    }
}

