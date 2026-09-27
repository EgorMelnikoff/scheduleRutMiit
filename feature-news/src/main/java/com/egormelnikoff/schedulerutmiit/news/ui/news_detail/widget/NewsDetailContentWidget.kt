package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn

@Composable
fun NewsDetailContentWidget(
    news: NewsParsedDto
) {
    Scaffold { contentPadding ->
        ScaffoldLazyColumn(
            horizontalSpacing = 0.dp,
            itemSpacing = 12.dp,
            contentPadding = PaddingValues(
                bottom = contentPadding.calculateBottomPadding()
            )
        ) {
            newsDetailImageWidget(news)
            newsDetailHeaderWidget(news)
            newsDetailTextWidget(news)
            newsDetailImagesWidget(news)
        }
    }
}