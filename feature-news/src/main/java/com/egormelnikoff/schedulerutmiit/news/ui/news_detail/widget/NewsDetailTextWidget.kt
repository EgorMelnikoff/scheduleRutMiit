package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.news.domain.model.News

internal fun LazyListScope.newsDetailTextWidget(
    news: News
) {
    items(news.paragraphs) { paragraph ->
        when (paragraph.first) {
            "p", "li" -> {
                val text = paragraph.second as AnnotatedString
                Text(
                    modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16),
                    text = text,
                    style = AppTheme.typography.bodyLarge,
                    color = AppTheme.colors.onBackground
                )
            }

            "tr" -> {
                val tableRow = paragraph.second as List<*>

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppTheme.paddings.padding4, horizontal = AppTheme.paddings.padding16),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding4)
                ) {
                    tableRow.forEach { text ->
                        Text(
                            text = text.toString(),
                            style = AppTheme.typography.bodyLarge,
                            color = AppTheme.colors.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}