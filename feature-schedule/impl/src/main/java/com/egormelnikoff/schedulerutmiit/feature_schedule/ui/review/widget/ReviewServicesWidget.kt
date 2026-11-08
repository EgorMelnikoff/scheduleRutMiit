package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ExpandedItem
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState

fun LazyListScope.reviewServicesWidget(
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
                                title = stringResource(R.string.news),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.news),
                                        contentDescription = null,
                                        tint = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                            ) {
                                onAction(ReviewAction.OnNewsCLicked)
                            }
                        }, {
                            AppCell(
                                title = stringResource(R.string.curriculum),
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.person),
                                        contentDescription = null,
                                        tint = AppTheme.colors.onSecondaryContainer
                                    )
                                },
                            ) {
                                onAction(ReviewAction.OnCurriculumClicked)
                            }
                        }
                    )
                )
            }
        }
    }
}