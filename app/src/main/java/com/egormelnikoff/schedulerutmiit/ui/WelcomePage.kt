package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.containers.PagerScreenContainer
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme

@Composable
fun WelcomePage(
    onSkip: () -> Unit
) {
    Scaffold { padding ->
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(
            pageCount = { 4 },
            initialPage = 0
        )

        PagerScreenContainer(
            pagerState = pagerState,
            scope = scope,
            isNextEnabled = { true },
            onFinish = onSkip,
            onCancel = onSkip,
            cancelTitle = stringResource(R.string.skip),
            finishTitle = stringResource(R.string.start) + "!",
            paddingValues = padding
        ) { page ->
            when (page) {
                0 -> Page(
                    title = stringResource(R.string.welcome_title),
                    titleSize = 30.sp,
                    subtitle = stringResource(R.string.welcome_subtitle)
                )


                1 -> Page(
                    title = stringResource(R.string.schedule_title),
                    subtitle = stringResource(R.string.schedule_subtitle),
                    painter = painterResource(R.drawable.schedules),
                )


                2 -> Page(
                    title = stringResource(R.string.features_title),
                    subtitle = stringResource(R.string.features_subtitle),
                    painter = painterResource(R.drawable.comment_and_tag),
                )


                3 -> Page(
                    title = stringResource(R.string.widget_title),
                    subtitle = stringResource(R.string.widget_subtitle),
                    painter = painterResource(R.drawable.widget),
                )
            }
        }
    }
}

@Composable
fun Page(
    title: String,
    titleSize: TextUnit = 26.sp,
    subtitle: String,
    imageHeight: Dp = 200.dp,
    painter: Painter? = null,
) {
    Box(
        modifier = Modifier.padding(horizontal = AppTheme.paddings.padding20)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            painter?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clip(AppTheme.shapes.extraLarge),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                SpacerHeight(AppTheme.paddings.padding16)
            }

            Text(
                text = title,
                color = AppTheme.colors.onBackground,
                style = AppTheme.typography.titleLarge,
                fontSize = titleSize
            )
            Text(
                text = subtitle,
                color = AppTheme.colors.onSecondaryContainer,
                style = AppTheme.typography.bodyMedium,
            )
        }
    }
}
