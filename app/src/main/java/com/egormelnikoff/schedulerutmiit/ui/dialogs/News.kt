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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.navigation.AppBackStack
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.news.DateNews
import com.egormelnikoff.schedulerutmiit.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsState

@Composable
fun NewsDialog(
    setDefaultState: () -> Unit,
    newsState: NewsState,
    appBackStack: AppBackStack,
) {
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
                    onClick = {
                        appBackStack.onBack()
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
                        text = news.title.trim(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
                    )
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        DateNews(
                            date = news.hisdateDisplay
                        )
                    }
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

                        if (news.images != null && news.images!!.size > 1) {
                            LazyRow(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(news.images!!) { link ->
                                    Image(
                                        modifier = Modifier
                                            .height(200.dp)
                                            .width(300.dp)
                                            .clip(MaterialTheme.shapes.extraLarge)
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
                                        painter = rememberAsyncImagePainter(link),
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
