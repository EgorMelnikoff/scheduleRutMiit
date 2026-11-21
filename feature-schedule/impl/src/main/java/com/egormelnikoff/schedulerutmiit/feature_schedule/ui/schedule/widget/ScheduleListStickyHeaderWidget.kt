package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

fun LazyListScope.scheduleListStickyHeaderWidget(
    date: LocalDate,
    formatter: DateTimeFormatter
) {
    stickyHeader {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.background)
                .padding(vertical = AppTheme.paddings.padding12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            val color = AppTheme.colors.primary

            Icon(
                modifier = Modifier.width(16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.calendar),
                contentDescription = null,
                tint = color
            )

            Text(
                text = "${
                    date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT_STANDALONE,
                        LocalLocale.current.platformLocale
                    ).replaceFirstChar { it.uppercase() }
                }, ${date.format(formatter)}",
                style = AppTheme.typography.titleMedium,
                color = color
            )
        }
    }
}