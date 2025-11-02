package com.egormelnikoff.schedulerutmiit.ui.screens.news

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.model.NewsShort
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.theme.StatusBarProtection
import com.egormelnikoff.schedulerutmiit.view_models.news.NewsUiState
import kotlinx.coroutines.flow.Flow
import java.time.format.DateTimeFormatter

@Composable
fun NewsScreen(
    onShowDialogNews: () -> Unit,
    onGetNewsById: (Long) -> Unit,

    newsUiState: NewsUiState,
    newsListFLow: Flow<PagingData<NewsShort>>,
    newsGridListState: LazyStaggeredGridState,
    externalPadding: PaddingValues,
) {
    val newsList = newsListFLow.collectAsLazyPagingItems()
    when {
        newsUiState.isLoading || newsList.loadState.refresh is LoadState.Loading -> {
            LoadingScreen(
                paddingTop = 0.dp,
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        newsUiState.error != null || newsList.loadState.refresh is LoadState.Error -> {
            val e = newsList.loadState.refresh as LoadState.Error

            ErrorScreen(
                title = LocalContext.current.getString(R.string.error),
                subtitle = newsUiState.error ?: e.error.localizedMessage,
                button = {
                    CustomButton(
                        buttonTitle = LocalContext.current.getString(R.string.repeat),
                        imageVector = ImageVector.vectorResource(R.drawable.refresh),
                        onClick = {
                            newsList.refresh()
                        }
                    )
                },
                paddingTop = 0.dp,
                paddingBottom = externalPadding.calculateBottomPadding()
            )
        }

        else -> {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(minSize = 300.dp),
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = externalPadding.calculateTopPadding() + 16.dp,
                    bottom = externalPadding.calculateBottomPadding()
                ),
                state = newsGridListState
            ) {
                items(newsList.itemCount) { index ->
                    val newsShort = newsList[index]
                    if (newsShort != null) {
                        NewsShort(
                            newsShort = newsShort,
                            onClick = {
                                onGetNewsById(newsShort.idInformation)
                                onShowDialogNews()
                            }
                        )
                    }
                }
                if (newsList.loadState.append is LoadState.Loading) {
                    item(
                        span = StaggeredGridItemSpan.FullLine
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 32.dp),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
    StatusBarProtection()
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(
                onClick = onClick
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
                text = newsShort.title.trim(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
            DateNews(
                date = newsShort.date.format(formatter)
            )
        }
    }
}

@Composable
fun DateNews(
    date: String
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = ImageVector.vectorResource(R.drawable.schedule),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}