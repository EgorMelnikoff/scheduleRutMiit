package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints.newsImageUrl

fun LazyListScope.newsDetailImagesWidget(
    news: NewsParsedDto
) {
    item {
        if (news.images.size > 1) {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(news.images) { link ->
                    Image(
                        modifier = Modifier
                            .height(200.dp)
                            .width(300.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        painter = rememberAsyncImagePainter(
                            newsImageUrl(
                                url = link,
                                width = 500
                            )
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

    }
}