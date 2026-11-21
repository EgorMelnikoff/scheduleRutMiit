package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints.newsImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.news.domain.model.News

internal fun LazyListScope.newsDetailImageWidget(
    news: News
) {
    item {
        if (news.images.isNotEmpty()) {
            val model =
                rememberAsyncImagePainter(newsImageUrl(url = news.images.first(), width = 500))
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(270.dp)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(270.dp)
                        .background(AppTheme.colors.secondaryContainer)
                        .alpha(transition),
                    painter = model,
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
                                    AppTheme.colors.background
                                )
                            )
                        )
                )
            }
        }
    }
}