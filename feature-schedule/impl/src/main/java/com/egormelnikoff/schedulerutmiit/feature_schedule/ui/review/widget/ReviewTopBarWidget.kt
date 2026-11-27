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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.core.common.R as CommonR
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthYearFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.R
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.DayPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewTopBarWidget(
    scrollBehavior: TopAppBarScrollBehavior,
    overlappedFraction: Float,
    showTopBarActions: Boolean,
    usedPhoto: Boolean,
    reviewState: ReviewState,
    onAction: (ReviewAction) -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (usedPhoto) {
            lerp(
                start = AppTheme.colors.onPrimary,
                stop = AppTheme.colors.onBackground,
                fraction = overlappedFraction
            )
        } else AppTheme.colors.onBackground,
        label = "TopAppBarContentColor"
    )

    AppTopBar(
        titleText = when (reviewState.dayPeriod) {
            DayPeriod.MORNING -> stringResource(R.string.schedule_good_morning)
            DayPeriod.DAY -> stringResource(R.string.schedule_good_afternoon)
            DayPeriod.EVENING -> stringResource(R.string.schedule_good_evening)
            DayPeriod.NIGHT -> stringResource(R.string.schedule_good_night)
        } + "!",
        subtitleText = reviewState.summaryState?.let { reviewData ->
            buildString {
                when (reviewData.date) {
                    reviewState.currentDate -> append(stringResource(R.string.schedule_today))
                    reviewState.currentDate?.plusDays(1) -> append(stringResource(R.string.schedule_tomorrow))
                    else -> reviewData.date.format(dayMonthYearFormatter)
                }

                append(" ")

                if (reviewData.events.isEmpty()) {
                    append(stringResource(R.string.schedule_no_events).replaceFirstChar { it.lowercase() })
                } else {
                    append(
                        "${reviewData.events.size} " + pluralStringResource(
                            R.plurals.schedule_events,
                            reviewData.events.size
                        )
                    )
                }
            }
        },
        scrollBehavior = if (usedPhoto) scrollBehavior else null,
        contentColor = contentColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
        ) {
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = !usedPhoto || showTopBarActions
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    IconButton(
                        onClick = {
                            onAction(ReviewAction.OnFindScheduleClicked)
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CommonR.drawable.search),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            onAction(ReviewAction.OnCreateScheduleClicked)
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CommonR.drawable.add),
                            contentDescription = null
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    onAction(ReviewAction.OnSettingsClicked)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(CommonR.drawable.settings_fill),
                    contentDescription = null
                )
            }
        }
    }
}