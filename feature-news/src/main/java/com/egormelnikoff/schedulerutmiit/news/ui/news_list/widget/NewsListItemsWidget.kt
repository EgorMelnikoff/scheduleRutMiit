package com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints.newsImageUrl
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsShort
import com.egormelnikoff.schedulerutmiit.news.ui.common.DateNewsWidget
import com.egormelnikoff.schedulerutmiit.news.ui.news_list.NewsListAction

internal fun LazyStaggeredGridScope.newsListItemsWidget(
    newsList: LazyPagingItems<NewsShort>,
    onAction: (NewsListAction) -> Unit
) {
    items(newsList.itemCount) { index ->
        val news = newsList[index]
        if (news != null) {
            val model = rememberAsyncImagePainter(newsImageUrl(news.picUrl))
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable(
                        onClick = {
                            onAction(NewsListAction.OnNewsClick(news.id))
                        }
                    )
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(transition)
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentScale = ContentScale.Crop,
                    painter = model,
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = news.title.trim(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                    DateNewsWidget(
                        date = news.date.format(dayMonthNameFormatter)
                    )
                }
            }
        }
    }
}
