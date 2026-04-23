package com.egormelnikoff.schedulerutmiit.ui.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PagerScreenContainer(
    pagerState: PagerState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    isNextEnabled: (Int) -> Boolean,
    onFinish: () -> Unit,
    showIndicator: Boolean = true,
    onCancel: (() -> Unit)? = null,
    backTitle: String = stringResource(R.string.back),
    nextTitle: String = stringResource(R.string.forward),
    finishTitle: String = stringResource(R.string.ready),
    cancelTitle: String = stringResource(R.string.cancel),
    paddingValues: PaddingValues = PaddingValues(0.dp),
    content: @Composable (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = paddingValues.calculateBottomPadding() + 8.dp)
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = paddingValues.calculateTopPadding() + 4.dp
            ),
            userScrollEnabled = isNextEnabled(pagerState.currentPage),
            pageSpacing = 24.dp
        ) { page ->
            content(page)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            val alphaBack by animateFloatAsState(
                targetValue = if (pagerState.isFirstPage()) (if (onCancel != null) 1f else 0f) else 1f,
                label = "alpha"
            )

            AnimatedContent(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .graphicsLayer { alpha = alphaBack },
                targetState = pagerState.isFirstPage(),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { state ->
                TextButton(
                    modifier = Modifier,
                    colors = ButtonDefaults.textButtonColors().copy(
                        containerColor = Color.Unspecified,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),

                    enabled = !state || onCancel != null,
                    onClick = {
                        if (state) {
                            onCancel?.invoke()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }
                ) {
                    Text(if (state) cancelTitle else backTitle)
                }
            }
            if (showIndicator) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val isSelected = pagerState.currentPage == iteration

                        val size by animateDpAsState(
                            targetValue = if (isSelected) 12.dp else 8.dp,
                            label = "size"
                        )

                        val color by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.onSecondaryContainer,
                            label = "color"
                        )

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(iteration)
                                    }
                                }
                                .size(size)
                        )
                    }
                }
            }

            AnimatedContent(
                modifier = Modifier.align(Alignment.CenterEnd),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                targetState = pagerState.isLastPage()
            ) { state ->
                Button(
                    enabled = isNextEnabled(pagerState.currentPage),
                    colors = ButtonDefaults.textButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        if (state) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                ) {
                    Text(if (state) finishTitle else nextTitle)
                }
            }
        }
    }
}

fun PagerState.isLastPage(): Boolean {
    return this.currentPage == this.pageCount.minus(1)
}

fun PagerState.isFirstPage(): Boolean {
    return this.currentPage == 0
}
