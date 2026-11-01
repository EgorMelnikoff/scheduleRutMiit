package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.widget

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
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.common.ModalDialogNamedScheduleHeader
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScheduleSheet(
    namedSchedule: NamedSchedule,
    onAction: (ReviewAction) -> Unit,
) {
    AppBottomSheet(
        isDarkTheme = true,
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
            modifier = Modifier.padding(horizontal = 16.dp),
            items = buildList {
                if (!namedSchedule.isDefault) {
                    add {
                        AppCell(
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
                            onAction(
                                ReviewAction.OnSetNamedSchedule(
                                    namedScheduleId = namedSchedule.id,
                                    setDefault = true
                                )
                            )
                        }

                    }
                }

                add {
                    AppCell(
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
                        onAction(
                            ReviewAction.OnSetNamedSchedule(
                                namedScheduleId = namedSchedule.id,
                                setDefault = false
                            )
                        )
                    }
                }

            }
        )
    }
}