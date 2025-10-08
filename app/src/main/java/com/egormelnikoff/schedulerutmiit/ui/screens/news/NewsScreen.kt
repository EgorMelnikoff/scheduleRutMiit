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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.model.NewsShort
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.screens.ErrorScreen
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.news.NewsState
import java.time.format.DateTimeFormatter

@Composable
fun NewsScreen(
    onShowDialogNews: () -> Unit,
    onGetNewsList: (Int) -> Unit,
    onGetNewsById: (Long) -> Unit,

    newsUiState: NewsState,
    newsGridListState: LazyStaggeredGridState,
    externalPadding: PaddingValues,
) {
    Box {
        when {
            newsUiState.newsList.isNotEmpty() -> {
                LazyVerticalStaggeredGrid (
                    modifier = Modifier.fillMaxWidth(),
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
                    items(newsUiState.newsList) { newsShort ->
                        NewsShort(
                            newsShort = newsShort,
                            onClick = {
                                onGetNewsById(newsShort.idInformation)
                                onShowDialogNews()
                            }
                        )
                    }
                }
            }

            newsUiState.isLoading -> LoadingScreen(
                paddingTop = 0.dp,
                paddingBottom = externalPadding.calculateBottomPadding()
            )

            newsUiState.isError ->
                ErrorScreen(
                    title = LocalContext.current.getString(R.string.error),
                    subtitle = LocalContext.current.getString(R.string.unable_load_news_list),
                    button = {
                        CustomButton(
                            buttonTitle = LocalContext.current.getString(R.string.repeat),
                            imageVector = ImageVector.vectorResource(R.drawable.refresh),
                            onClick = {
                                onGetNewsList(1)
                            }
                        )
                    },
                    paddingTop = 0.dp,
                    paddingBottom = externalPadding.calculateBottomPadding()
                )
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
            modifier = Modifier
                .fillMaxWidth()
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

@Composable
fun DateNews(
    date: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 2.dp),
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
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
