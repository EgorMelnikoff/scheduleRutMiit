package com.egormelnikoff.schedulerutmiit.ui.news

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import com.egormelnikoff.schedulerutmiit.ui.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.classes.News
import com.egormelnikoff.schedulerutmiit.classes.NewsShort
import com.egormelnikoff.schedulerutmiit.ui.view_models.NewsListState
import com.egormelnikoff.schedulerutmiit.ui.view_models.NewsState
import com.egormelnikoff.schedulerutmiit.ui.view_models.NewsViewModel
import java.time.format.DateTimeFormatter

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel,
    showDialogNews: Boolean,
    onShowDialogNews: (Boolean) -> Unit,
    stateNewsList: NewsListState,
    stateNews: NewsState,

    newsListState: LazyListState,
    newsDialogState: ScrollState,
    paddingValues: PaddingValues
) {
    if (stateNewsList !is NewsListState.Loaded) {
        newsViewModel.getNewsList(1)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = stateNewsList,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { targetNewsListState ->
            when (targetNewsListState) {
                is NewsListState.Error -> ErrorScreen(
                    title = LocalContext.current.getString(R.string.error),
                    subtitle = LocalContext.current.getString(R.string.unable_load_news_list),
                    buttonTitle = LocalContext.current.getString(R.string.repeat),
                    imageVector = ImageVector.vectorResource(R.drawable.refresh),
                    action = {
                        newsViewModel.getNewsList(1)
                    }
                )

                is NewsListState.Loading -> LoadingScreen()
                is NewsListState.Loaded -> {
                    LazyColumn(
                        state = newsListState,
                        modifier = Modifier.padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    ) {
                        items(targetNewsListState.news) { newsShort ->
                            NewsShort(
                                newsShort = newsShort,
                                onClick = {
                                    newsViewModel.getNewsById(newsShort.idInformation)
                                    onShowDialogNews(true)
                                }
                            )
                        }
                    }
                    AnimatedVisibility(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
                        visible = showDialogNews,
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        when (stateNews) {
                            is NewsState.Error -> Empty(
                                subtitle = LocalContext.current.getString(R.string.error),
                            )

                            is NewsState.Loading -> LoadingScreen()
                            is NewsState.Loaded -> {
                                DialogNews(
                                    newsDialogState = newsDialogState,
                                    news = stateNews.news,
                                )
                            }
                        }
                        BackHandler {
                            onShowDialogNews(false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsShort(
    newsShort: NewsShort,
    onClick: () -> Unit
) {
    val model = rememberAsyncImagePainter(newsShort.thumbnail)
    val transition by animateFloatAsState(
        targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
    )
    val formatter = DateTimeFormatter.ofPattern("d MMMM")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                onClick = onClick
            )
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(transition)
                .height(150.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop,
            painter = model,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(138.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = newsShort.title.trim(),
                    fontSize = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                DateNews(
                    date = newsShort.date.format(formatter)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNews(
    newsDialogState: ScrollState,
    news: News
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val spacerHeight = if (!news.images.isNullOrEmpty()) {
            230.dp
        } else 0.dp
        if (!news.images.isNullOrEmpty()) {
            val model = rememberAsyncImagePainter(news.images!!.first())
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .alpha(transition)
                    .background(MaterialTheme.colorScheme.surface),
                painter = model,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier.verticalScroll(newsDialogState)
        ) {
            Spacer(modifier = Modifier.height(spacerHeight))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
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
                val uriHandler = LocalUriHandler.current


                Column (
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    news.elements?.forEach { element ->
                        when (element.first) {
                            "p", "li" -> {
                                val text = element.second as AnnotatedString
                                ClickableText(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                    onClick = { offset ->
                                        text.getStringAnnotations(
                                            tag = "URL",
                                            start = offset,
                                            end = offset
                                        )
                                            .firstOrNull()?.let { annotation ->
                                                uriHandler.openUri(annotation.item)
                                            }
                                    },

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
                                    tableRow.forEach {text ->
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

            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spacerHeight)
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

@Composable
fun DateNews(
    date: String
) {
    Row(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = ImageVector.vectorResource(R.drawable.schedule),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = date,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
