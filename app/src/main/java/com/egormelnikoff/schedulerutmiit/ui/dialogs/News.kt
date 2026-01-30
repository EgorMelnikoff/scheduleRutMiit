package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.news.DateNews
import com.egormelnikoff.schedulerutmiit.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsState

@Composable
fun NewsDialog(
    setDefaultState: () -> Unit,
    newsState: NewsState,
    navigationActions: NavigationActions
) {
    when {
        newsState.isLoading -> LoadingScreen(
            paddingTop = 0.dp,
            paddingBottom = 0.dp
        )


        newsState.error != null -> ErrorScreen(
            title = stringResource(R.string.error),
            subtitle = newsState.error,
            button = {
                CustomButton(
                    modifier = Modifier.fillMaxWidth(),
                    buttonTitle = stringResource(R.string.back),
                    imageVector = ImageVector.vectorResource(R.drawable.back),
                    onClick = {
                        navigationActions.onBack()
                        setDefaultState()
                    },
                )
            }
        )

        newsState.currentNews != null -> {
            NewsDialogContent(
                news = newsState.currentNews
            )
        }
    }
    StatusBarProtection()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDialogContent(
    news: News
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val spacerHeight = if (!news.images.isNullOrEmpty()) {
                230.dp
            } else paddingValues.calculateTopPadding()
            if (!news.images.isNullOrEmpty()) {
                val model = rememberAsyncImagePainter(news.images!!.first())
                val transition by animateFloatAsState(
                    targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
                )
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
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
                        .clip(
                            MaterialTheme.shapes.extraLarge.copy(
                                bottomStart = CornerSize(0), bottomEnd = CornerSize(0)
                            )
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = news.title.trim(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
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
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
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
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
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
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    painter = modelListImages,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                    )
                }
            }
        }
    }
}
