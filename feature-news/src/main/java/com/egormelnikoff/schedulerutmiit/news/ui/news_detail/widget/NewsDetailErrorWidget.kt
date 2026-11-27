package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.egormelnikoff.schedulerutmiit.core.common.error.getMessage
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ErrorScreen
import com.egormelnikoff.schedulerutmiit.news.R
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDetailAction
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDetailState

@Composable
internal fun NewsDetailErrorWidget(
    newsDetailState: NewsDetailState.Error,
    onAction: (NewsDetailAction) -> Unit
) {
    ErrorScreen(
        title = stringResource(R.string.news_error),
        subtitle = newsDetailState.appError.getMessage(LocalContext.current),
        button = {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.news_retry),
                imageVector = AppTheme.icons.refresh,
                onClick = {
                    onAction(NewsDetailAction.OnRetryClicked)
                },
            )
        }
    )
}