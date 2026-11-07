package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.DayPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewTopBarWidget(
    scrollBehavior: TopAppBarScrollBehavior,
    overlappedFraction: Float,
    showTopBarActions: Boolean,
    reviewState: ReviewState,
    onAction: (ReviewAction) -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (reviewState.usedPhoto) {
            lerp(
                start = MaterialTheme.colorScheme.onPrimary,
                stop = MaterialTheme.colorScheme.onBackground,
                fraction = overlappedFraction
            )
        } else MaterialTheme.colorScheme.onBackground,
        label = "TopAppBarContentColor"
    )

    AppTopBar(
        titleText = when (reviewState.dayPeriod) {
            DayPeriod.MORNING -> stringResource(R.string.good_morning)
            DayPeriod.DAY -> stringResource(R.string.good_afternoon)
            DayPeriod.EVENING -> stringResource(R.string.good_evening)
            DayPeriod.NIGHT -> stringResource(R.string.good_night)
        } + "!",
        subtitleText = reviewState.summaryState?.let { reviewData ->
            buildString {
                when (reviewData.date) {
                    reviewState.currentDate -> append(stringResource(R.string.today))
                    reviewState.currentDate?.plusDays(1) -> append(stringResource(R.string.tomorrow))
                    else -> reviewData.date.format(dayMonthYearFormatter)
                }

                append(" ")

                if (reviewData.events.isEmpty()) {
                    append(stringResource(R.string.no_events).replaceFirstChar { it.lowercase() })
                } else {
                    append(
                        "${reviewData.events.size} " + pluralStringResource(
                            R.plurals.events,
                            reviewData.events.size
                        )
                    )
                }
            }
        },
        scrollBehavior = if (reviewState.usedPhoto) scrollBehavior else null,
        contentColor = contentColor
    ) {
        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = !reviewState.usedPhoto || showTopBarActions
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {
                        onAction(ReviewAction.OnFindScheduleClicked)
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        onAction(ReviewAction.OnCreateScheduleClicked)
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.add),
                        contentDescription = null
                    )
                }
            }
        }
    }
}