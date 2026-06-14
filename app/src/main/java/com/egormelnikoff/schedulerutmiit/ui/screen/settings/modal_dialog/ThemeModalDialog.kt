package com.egormelnikoff.schedulerutmiit.ui.screen.settings.modal_dialog

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
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColorSelector
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomSwitch
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ThemeSelector
import com.egormelnikoff.schedulerutmiit.core.ui.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    preferencesViewModel: PreferencesViewModel
) {
    CustomModalBottomSheet(
        isDarkTheme = appSettings.decorPreferences.theme.isDarkTheme(),
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
                                preferencesViewModel.onSetTheme(value)
                            },
                            currentTheme = appSettings.decorPreferences.theme
                        )
                    }
                }, {
                    ClickableItem(
                        title = stringResource(R.string.use_amoled),
                        defaultMinHeight = 36.dp,
                        trailingIcon = {
                            CustomSwitch(
                                checked = appSettings.decorPreferences.usedAmoled,
                                enabled = appSettings.decorPreferences.theme.isDarkTheme(),
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
                ColorSelector(
                    currentSelected = appSettings.decorPreferences.decorColorIndex
                ) { value ->
                    preferencesViewModel.onSetDecorColor(value)
                }

            }
        )
    }
}

