package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.model.News
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.news.DateNews
import com.egormelnikoff.schedulerutmiit.ui.view_models.news.NewsState

@Composable
fun NewsDialog(
    newsUiState: NewsState,
    externalPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            newsUiState.isLoading -> LoadingScreen(
                paddingTop = 0.dp,
                paddingBottom = 0.dp
            )

            newsUiState.isError -> Empty(
                subtitle = LocalContext.current.getString(R.string.error),
                paddingTop = 0.dp,
                paddingBottom = 0.dp
            )

            newsUiState.currentNews != null -> {
                NewsDialogContent(
                    news = newsUiState.currentNews,
                    paddingTop = externalPadding.calculateTopPadding(),
                    paddingBottom = externalPadding.calculateBottomPadding()
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDialogContent(
    news: News,
    paddingTop: Dp,
    paddingBottom: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val spacerHeight = if (!news.images.isNullOrEmpty()) {
            230.dp
        } else paddingTop
        if (!news.images.isNullOrEmpty()) {
            val model = rememberAsyncImagePainter(news.images!!.first())
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .alpha(transition),
                painter = model,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(spacerHeight))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = news.title.trim(),
                    fontSize = 20.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                DateNews(
                    date = news.hisdateDisplay
                )
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    news.elements?.forEach { element ->
                        when (element.first) {
                            "p", "li" -> {
                                val text = element.second as AnnotatedString
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                            }

                            "tr" -> {
                                val tableRow = element.second as List<*>
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    tableRow.forEach { text ->
                                        Text(
                                            text = text.toString(),
                                            style = TextStyle(
                                                platformStyle = PlatformTextStyle(
                                                    includeFontPadding = false
                                                )
                                            ),
                                            fontSize = 14.sp,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }

                            }
                        }
                    }

                    if (news.images != null && news.images!!.size > 1) {
                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { news.images!!.count() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            preferredItemWidth = 250.dp,
                            itemSpacing = 8.dp
                        ) { i ->
                            val modelListImages = rememberAsyncImagePainter(news.images!![i])
                            Image(
                                modifier = Modifier
                                    .height(200.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .background(MaterialTheme.colorScheme.surface),
                                painter = modelListImages,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(
                    modifier = Modifier.padding(bottom = paddingBottom)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 200f
                    )
                )
        )
    }
}
