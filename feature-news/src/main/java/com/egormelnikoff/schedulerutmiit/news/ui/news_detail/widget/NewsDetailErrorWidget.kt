package com.egormelnikoff.schedulerutmiit.news.ui.news_detail.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.error.getMessage
import com.egormelnikoff.schedulerutmiit.core.ui.components.buttom.AppButton
import com.egormelnikoff.schedulerutmiit.core.ui.widget.ErrorScreen
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDetailAction
import com.egormelnikoff.schedulerutmiit.news.ui.news_detail.NewsDetailState

@Composable
internal fun NewsDetailErrorWidget(
    newsDetailState: NewsDetailState.Error,
    onAction: (NewsDetailAction) -> Unit
) {
    ErrorScreen(
        title = stringResource(R.string.error),
        subtitle = newsDetailState.appError.getMessage(LocalContext.current),
        button = {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.retry),
                imageVector = ImageVector.vectorResource(R.drawable.refresh),
                onClick = {
                    onAction(NewsDetailAction.OnRetryClicked)
                },
            )
        }
    )
}