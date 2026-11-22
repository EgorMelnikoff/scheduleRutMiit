package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ExpandedItem
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState

internal fun LazyListScope.reviewServicesWidget(
    reviewState: ReviewState,
    onAction: (ReviewAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16)
        ) {
            ExpandedItem(
                title = stringResource(R.string.services),
                visible = reviewState.showServices,
                onChangeVisibility = { show ->
                    onAction(ReviewAction.OnShowServices(show))
                }
            ) {
                ColumnGroup(
                    items = listOf(
                        {
                            AppCell(
                                start = {
                                    Icon(
                                        icon = ImageVector.vectorResource(R.drawable.news),
                                        iconSize = 16.dp,
                                        iconColor = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                                center = {
                                    TextBlock(
                                        text = stringResource(R.string.news),
                                    )
                                },
                                onClick = {
                                    onAction(ReviewAction.OnNewsCLicked)
                                }
                            )
                        }, {
                            AppCell(
                                start = {
                                    Icon(
                                        icon = ImageVector.vectorResource(R.drawable.person),
                                        iconSize = 16.dp,
                                        iconColor = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                                center = {
                                    TextBlock(
                                        text = stringResource(R.string.curriculum),
                                    )
                                },
                                onClick = {
                                    onAction(ReviewAction.OnCurriculumClicked)
                                }
                            )
                        }, {
                            AppCell(
                                start = {
                                    Icon(
                                        icon = ImageVector.vectorResource(R.drawable.tasks),
                                        iconSize = 16.dp,
                                        iconColor = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                                center = {
                                    TextBlock(
                                        text = stringResource(R.string.tasks),
                                    )
                                },
                                onClick = {
                                    onAction(ReviewAction.OnTasksClicked)
                                }
                            )
                        }
                    )
                )
            }
        }
    }
}