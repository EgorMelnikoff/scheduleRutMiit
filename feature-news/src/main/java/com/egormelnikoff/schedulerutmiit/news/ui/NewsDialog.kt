package com.egormelnikoff.schedulerutmiit.news.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsParsedDto
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints.newsImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.ErrorScreen
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.core.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.news.view_model.NewsViewModel

@Composable
fun NewsDialog(
    newsDialog: Route.Dialog.NewsDialog,
    onBack: () -> Unit
) {
    val newsViewModel =
        hiltViewModel<NewsViewModel, NewsViewModel.Factory> { factory ->
            factory.create(newsDialog.newsId)
        }
    val newsState = newsViewModel.newsState.collectAsStateWithLifecycle().value


    when {
        newsState.isLoading -> LoadingScreen()

        newsState.error != null -> ErrorScreen(
            title = stringResource(R.string.error),
            subtitle = newsState.error,
            button = {
                CustomButton(
                    modifier = Modifier.fillMaxWidth(),
                    buttonTitle = stringResource(R.string.back),
                    imageVector = ImageVector.vectorResource(R.drawable.back),
                    onClick = onBack,
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

@Composable
fun NewsDialogContent(
    news: NewsParsedDto
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val spacerHeight = if (news.images.isNotEmpty()) {
                230.dp
            } else paddingValues.calculateTopPadding()

            if (news.images.isNotEmpty()) {
                val model =
                    rememberAsyncImagePainter(newsImageUrl(url = news.images.first(), width = 500))
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
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = MaterialTheme.shapes.extraLarge.copy(
                                bottomStart = CornerSize(0), bottomEnd = CornerSize(0)
                            )
                        )
                        .padding(
                            top = 16.dp,
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = news.newsDto.title.trim(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
                    )
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        DateNews(
                            date = news.newsDto.date
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        news.elements.forEach { element ->
                            when (element.first) {
                                "p", "li" -> {
                                    val text = element.second as AnnotatedString
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
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
                                            .padding(vertical = 4.dp, horizontal = 16.dp),
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
            }
        }
    }
}
