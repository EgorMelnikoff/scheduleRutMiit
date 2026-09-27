package com.egormelnikoff.schedulerutmiit.ui.setting_screen.modal_dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.enums.EventsCountView
import com.egormelnikoff.schedulerutmiit.core.common.preferences.AppSettings
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountEventsModalDialog(
    onDismiss: () -> Unit,
    appSettings: AppSettings,
    preferencesViewModel: PreferencesViewModel
) {
    AppBottomSheet(
        modifier = Modifier.padding(horizontal = 16.dp),
        onDismiss = onDismiss
    ) {
        ColumnGroup(
            title = stringResource(R.string.show_count_classes),
            items = EventsCountView.entries.map {
                {
                    AppCell(
                        title = when (it) {
                            EventsCountView.DETAILS -> stringResource(R.string.details)
                            EventsCountView.BRIEFLY -> stringResource(R.string.briefly)
                            EventsCountView.OFF -> stringResource(R.string.dont_show)
                        },
                        subtitle =  when (it) {
                            EventsCountView.DETAILS -> stringResource(R.string.display_each_lesson)
                            EventsCountView.BRIEFLY -> stringResource(R.string.display_number_lessons)
                            EventsCountView.OFF -> null
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = when (it) {
                                    EventsCountView.DETAILS -> ImageVector.vectorResource(R.drawable.points)
                                    EventsCountView.BRIEFLY -> ImageVector.vectorResource(R.drawable.one)
                                    EventsCountView.OFF -> ImageVector.vectorResource(R.drawable.visibility_off)
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        trailingIcon = {
                            RadioButton(
                                selected = (it == appSettings.eventsCountView),
                                onClick = {
                                    preferencesViewModel.onSetEventsCountView(it)
                                }
                            )
                        },
                        showClickLabel = false
                    ) {
                        preferencesViewModel.onSetEventsCountView(it)
                    }
                }

            }
        )
    }
}