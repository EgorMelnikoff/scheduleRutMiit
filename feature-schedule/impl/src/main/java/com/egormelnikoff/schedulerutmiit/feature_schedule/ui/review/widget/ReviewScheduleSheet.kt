package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R as CommonR
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.R
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common.ModalDialogNamedScheduleHeader
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewScheduleSheet(
    namedSchedule: NamedSchedule,
    onAction: (ReviewAction) -> Unit,
) {
    AppBottomSheet(
        isDarkTheme = true,
        showHandle = false,
        contentPadding = PaddingValues(
            top = AppTheme.paddings.padding8,
            bottom = AppTheme.paddings.padding12
        ),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding16),
        onDismiss = {
            onAction(
                ReviewAction.OnShowScheduleSheet(null)
            )
        }
    ) {
        ModalDialogNamedScheduleHeader(
            namedSchedule = namedSchedule,
            schedule = null,
            isSavedNamedSchedule = true,

            onDeleteNamedSchedule = {
                onAction(
                    ReviewAction.OnDeleteNamedSchedule(
                        namedSchedule.id,
                        namedSchedule.isDefault
                    )
                )
            },
            onOpenRenameDialog = {
                onAction(
                    ReviewAction.OnRenameScheduleClicked(
                        namedScheduleId = namedSchedule.id
                    )
                )
            },
            onDismiss = {
                onAction(
                    ReviewAction.OnShowScheduleSheet(null)
                )
            }
        )
        ColumnGroup(
            modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
            items = buildList {
                if (!namedSchedule.isDefault) {
                    add {
                        AppCell(

                            start = {
                                Icon(
                                    icon = ImageVector.vectorResource(CommonR.drawable.check),
                                    iconSize = 20.dp,
                                    iconColor = AppTheme.colors.onBackground
                                )
                            },
                            center = {
                                TextBlock(
                                    text = stringResource(R.string.schedule_make_default),
                                )
                            },
                            showChevron = false,
                            onClick = {
                                onAction(
                                    ReviewAction.OnSetNamedSchedule(
                                        namedScheduleId = namedSchedule.id,
                                        setDefault = true
                                    )
                                )
                            }
                        )
                    }
                }

                add {
                    AppCell(
                        start = {
                            Icon(
                                icon = ImageVector.vectorResource(CommonR.drawable.open_panel),
                                iconSize = 20.dp,
                                iconColor = AppTheme.colors.onBackground,
                            )
                        },
                        center = {
                            TextBlock(
                                text = stringResource(R.string.schedule_open),
                            )
                        },
                        onClick = {
                            onAction(
                                ReviewAction.OnSetNamedSchedule(
                                    namedScheduleId = namedSchedule.id,
                                    setDefault = false
                                )
                            )
                        }
                    )
                }
            }
        )
    }
}