package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.DayPeriod

fun LazyListScope.reviewImageWidget(
    reviewState: ReviewState
) {
    item {
        Box(
            Modifier
                .fillMaxWidth()
                .height(270.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = when (reviewState.dayPeriod) {
                    DayPeriod.MORNING -> painterResource(R.drawable.day)
                    DayPeriod.DAY -> painterResource(R.drawable.day)
                    DayPeriod.EVENING -> painterResource(R.drawable.evening)
                    DayPeriod.NIGHT -> painterResource(R.drawable.night)
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }
    }
}