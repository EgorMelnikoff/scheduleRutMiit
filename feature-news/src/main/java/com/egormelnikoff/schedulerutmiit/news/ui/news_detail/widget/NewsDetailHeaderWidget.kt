package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.ui.common.DateNewsWidget

internal fun LazyListScope.newsDetailHeaderWidget(
    news: News
) {
    item {
        Column(
            modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            Text(
                text = news.title.trim(),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4
            )
            DateNewsWidget(
                date = news.date
            )
        }
    }
}