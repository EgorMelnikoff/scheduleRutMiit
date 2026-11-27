package com.egormelnikoff.schedulerutmiit.news.ui.news_list.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.egormelnikoff.schedulerutmiit.core.common.error.getMessage
import com.egormelnikoff.schedulerutmiit.core.common.error.toAppError
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ErrorScreen
import com.egormelnikoff.schedulerutmiit.news.R
import com.egormelnikoff.schedulerutmiit.news.domain.model.NewsShort

@Composable
internal fun NewsListErrorWidget(
    newsList: LazyPagingItems<NewsShort>
) {
    val error = newsList.loadState.refresh as LoadState.Error
    val typedError = error.error.toAppError()

    ErrorScreen(
        title = stringResource(R.string.news_error),
        subtitle = typedError.getMessage(LocalContext.current),
        button = {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.news_repeat),
                imageVector = AppTheme.icons.refresh,
                onClick = {
                    newsList.refresh()
                }
            )
        }
    )
}