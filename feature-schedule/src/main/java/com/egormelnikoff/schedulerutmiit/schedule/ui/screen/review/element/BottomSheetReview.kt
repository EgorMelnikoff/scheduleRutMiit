package com.egormelnikoff.schedulerutmiit.schedule.ui.screen.review.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomModalBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.findDefault
import com.egormelnikoff.schedulerutmiit.schedule.ui.screen.schedule.element.ModalDialogNamedScheduleHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDialogReview(
    namedSchedule: NamedSchedule,
    schedulesWithEvents: List<ScheduleWithEvents>? = null,

    appBackStack: AppBackStack,
    isDefaultNamedSchedule: Boolean,

    onOpenNamedSchedule: ((Long, Boolean, Boolean) -> Unit)? = null,
    onDeleteNamedSchedule: (Long, Boolean) -> Unit,
    onDismiss: (NamedSchedule?) -> Unit
) {
    CustomModalBottomSheet(
        isDarkTheme = true,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        onDismiss = {
            onDismiss(null)
        }
    ) {
        ModalDialogNamedScheduleHeader(
            appBackStack = appBackStack,
            onDeleteNamedSchedule = onDeleteNamedSchedule,
            namedSchedule = namedSchedule,
            isSavedNamedSchedule = true,
            isDefaultNamedSchedule = isDefaultNamedSchedule,
            schedule = schedulesWithEvents?.findDefault()?.schedule,
            onDismiss = onDismiss
        )
        if (schedulesWithEvents == null) {
            ColumnGroup(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = buildList {
                    if (!isDefaultNamedSchedule && onOpenNamedSchedule != null) {
                        add {
                            ClickableItem(
                                title = stringResource(R.string.make_default),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.check),
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = null
                                    )
                                },
                                defaultMinHeight = 24.dp,
                                showClickLabel = false
                            ) {
                                onOpenNamedSchedule(namedSchedule.id, true, false)
                                onDismiss(null)
                            }

                        }
                    }
                    onOpenNamedSchedule?.let {
                        add {
                            ClickableItem(
                                title = stringResource(R.string.open),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.open_panel),
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = null
                                    )
                                },
                                defaultMinHeight = 24.dp
                            ) {
                                onOpenNamedSchedule(namedSchedule.id, false, true)
                                onDismiss(null)
                            }
                        }
                    }
                }
            )
        }
    }
}