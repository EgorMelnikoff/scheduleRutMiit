package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
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
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewAction

fun LazyListScope.reviewActionsWidget(
    onChangeTopBarVisibility: (Float) -> Unit,
    onAction: (ReviewAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier
                .padding(
                    vertical = 8.dp, horizontal = 16.dp
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
                            titleTypography = MaterialTheme.typography.titleSmall,
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.search),
                                    iconSize = 20.dp,
                                    color = MaterialTheme.colorScheme.onBackground
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
                            titleTypography = MaterialTheme.typography.titleSmall,
                            leadingIcon = {
                                LeadingIcon(
                                    imageVector = ImageVector.vectorResource(R.drawable.add),
                                    iconSize = 20.dp,
                                    color = MaterialTheme.colorScheme.onBackground
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