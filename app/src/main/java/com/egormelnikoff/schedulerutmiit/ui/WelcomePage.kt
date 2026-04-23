package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.PagerScreenContainer
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.SettingsViewModel


@Composable
fun WelcomePage(
    settingsViewModel: SettingsViewModel,
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
            onFinish = {
                settingsViewModel.skipWelcomePage()
            },
            onCancel = {
                settingsViewModel.skipWelcomePage()
            },
            cancelTitle = stringResource(R.string.skip),
            paddingValues = padding
        ) {page ->
            when (page) {
                0 -> {
                    Page(
                        title = stringResource(R.string.welcome_title),
                        subtitle = stringResource(R.string.welcome_subtitle)
                    )
                }

                1 -> {
                    Page(
                        title = stringResource(R.string.schedule_title),
                        subtitle = stringResource(R.string.schedule_subtitle),
                        painter = painterResource(R.drawable.schedules),
                    )
                }

                2 -> {
                    Page(
                        title = stringResource(R.string.features_title),
                        subtitle = stringResource(R.string.features_subtitle),
                        painter = painterResource(R.drawable.comment_and_tag),
                    )
                }

                3 -> {
                    Page(
                        title = stringResource(R.string.widget_title),
                        subtitle = stringResource(R.string.widget_subtitle),
                        painter = painterResource(R.drawable.widget),
                    )
                }
            }
        }
    }
}

@Composable
fun Page(
    height: Dp = 200.dp,
    painter: Painter? = null,
    title: String,
    subtitle: String,
) {
    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            painter?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .clip(MaterialTheme.shapes.extraLarge),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(16.dp))

            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 26.sp
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
