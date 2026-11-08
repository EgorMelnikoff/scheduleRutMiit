package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.LeadingIcon
import com.egormelnikoff.schedulerutmiit.core.ui.containers.RowGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction

fun LazyListScope.reviewActionsWidget(
    onChangeTopBarVisibility: (Float) -> Unit,
    onAction: (ReviewAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier
                .padding(
                    vertical = AppTheme.paddings.padding8, horizontal = AppTheme.paddings.padding16
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
                            defaultMinHeight = 32.dp,
                            showClickLabel = false,
                            title = "${stringResource(R.string.find)} ${stringResource(R.string.schedule).replaceFirstChar { it.lowercase() }}",
                            titleTypography = AppTheme.typography.titleSmall,
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.search),
                                    iconSize = 20.dp,
                                    color = AppTheme.colors.onBackground
                                )
                            }
                        ) {
                            onAction(ReviewAction.OnFindScheduleClicked)
                        }
                    }, {
                        AppCell(
                            defaultMinHeight = 32.dp,
                            showClickLabel = false,
                            title = "${stringResource(R.string.create)} ${
                                stringResource(
                                    R.string.schedule
                                ).replaceFirstChar { it.lowercase() }
                            }",
                            titleTypography = AppTheme.typography.titleSmall,
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.add),
                                    iconSize = 20.dp,
                                    color = AppTheme.colors.onBackground
                                )
                            }
                        ) {
                            onAction(ReviewAction.OnCreateScheduleClicked)
                        }
                    }
                )
            )
        }
    }
}