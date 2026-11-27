package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R as CommonR
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RowGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.R
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction

internal fun LazyListScope.reviewActionsWidget(
    onChangeTopBarVisibility: (Float) -> Unit,
    onAction: (ReviewAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier
                .padding(
                    vertical = AppTheme.paddings.padding8,
                    horizontal = AppTheme.paddings.padding16
                )
                .onGloballyPositioned { coordinates ->
                    val y = coordinates.positionInWindow().y

                    onChangeTopBarVisibility(y)
                }
        ) {
            RowGroup(
                items = listOf(
                    {
                        AppCell(
                            contentPadding = PaddingValues(
                                horizontal = AppTheme.paddings.padding12,
                                vertical = AppTheme.paddings.padding16
                            ),
                            start = {
                                Icon(
                                    icon = ImageVector.vectorResource(CommonR.drawable.search),
                                    iconSize = 20.dp,
                                    iconColor = AppTheme.colors.onBackground
                                )
                            },
                            center = {
                                TextBlock(
                                    text = "${stringResource(R.string.schedule_find)} ${stringResource(R.string.schedule_title).replaceFirstChar { it.lowercase() }}",
                                    typography = AppTheme.typography.titleSmall,
                                )
                            },
                            showChevron = false,
                            onClick = {
                                onAction(ReviewAction.OnFindScheduleClicked)
                            }
                        )
                    }, {
                        AppCell(
                            contentPadding = PaddingValues(
                                horizontal = AppTheme.paddings.padding12,
                                vertical = AppTheme.paddings.padding16
                            ),
                            start = {
                                Icon(
                                    icon = ImageVector.vectorResource(CommonR.drawable.add),
                                    iconSize = 20.dp,
                                    iconColor = AppTheme.colors.onBackground
                                )
                            },
                            center = {
                                TextBlock(
                                    text = "${stringResource(R.string.schedule_create)} ${
                                        stringResource(
                                            R.string.schedule_title
                                        ).replaceFirstChar { it.lowercase() }
                                    }",
                                    typography = AppTheme.typography.titleSmall,
                                )
                            },
                            showChevron = false,
                            onClick = {
                                onAction(ReviewAction.OnCreateScheduleClicked)
                            }
                        )
                    }
                )
            )
        }
    }
}