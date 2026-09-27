package com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.result.TypedErrorException
import com.egormelnikoff.schedulerutmiit.core.common.result.getMessage
import com.egormelnikoff.schedulerutmiit.core.network.dto.news.NewsShortDto
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ErrorScreen

@Composable
fun NewsListErrorWidget(
    newsList: LazyPagingItems<NewsShortDto>
) {
    val error = newsList.loadState.refresh as LoadState.Error
    val typedError = (error.error as? TypedErrorException)?.typedError

    ErrorScreen(
        title = stringResource(R.string.error),
        subtitle = typedError?.getMessage(LocalContext.current),
        button = {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.repeat),
                imageVector = ImageVector.vectorResource(R.drawable.refresh),
                onClick = {
                    newsList.refresh()
                }
            )
        }
    )
}