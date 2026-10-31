package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.news.domain.model.News
import com.egormelnikoff.schedulerutmiit.news.ui.common.DateNewsWidget

internal fun LazyListScope.newsDetailHeaderWidget(
    news: News
) {
    item {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = news.title.trim(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4
            )
            DateNewsWidget(
                date = news.date
            )
        }
    }
}