package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ExpandedItem
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common.SchedulesWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState

fun LazyListScope.reviewSchedulesWidget(
    reviewState: ReviewState,
    onAction: (ReviewAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ExpandedItem(
                title = stringResource(R.string.saved_schedules),
                visible = reviewState.showSchedules,
                onChangeVisibility = { show ->
                    onAction(ReviewAction.OnShowSchedules(show))
                }
            ) {
                SchedulesWidget(
                    namedSchedules = reviewState.namedSchedules,
                    onClickItem = { namedSchedule ->
                        onAction(
                            ReviewAction.OnShowScheduleSheet(
                                namedSchedule = namedSchedule
                            )
                        )
                    }
                )
            }
        }
    }
}