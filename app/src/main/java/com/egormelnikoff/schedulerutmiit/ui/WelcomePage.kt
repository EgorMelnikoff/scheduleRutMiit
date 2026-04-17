package com.egormelnikoff.schedulerutmiit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.view_models.settings.SettingsViewModel
import kotlinx.coroutines.launch


@Composable
fun WelcomePage(
    settingsViewModel: SettingsViewModel,
) {
    val scope = rememberCoroutineScope()
    val state = rememberPagerState(
        pageCount = { 4 },
        initialPage = 0
    )
    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = state
            ) { page ->
                when (page) {
                    0 -> {
                        Page(
                            title = "Добро пожаловать!",
                            subtitle = "Это приложение для поиска и сохранения расписаний РУТ(МИИТ)"
                        )
                    }

                    1 -> {
                        Page(
                            title = "Расписание",
                            subtitle = "Сохраненные расписания доступны даже без интернета!",
                            painter = painterResource(R.drawable.schedules),
                        )
                    }

                    2 -> {
                        Page(
                            title = "Виджет",
                            subtitle = "Также можно использовать виджет, который подскажет расписание пар!",
                            painter = painterResource(R.drawable.widget),
                        )
                    }

                    3 -> {
                        Page(
                            title = "Комментарии и теги",
                            subtitle = "К парам можно добавлять текстовые комментарии и цветовые теги",
                            painter = painterResource(R.drawable.comment_and_tag),
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = state.currentPage != state.pageCount.minus(1)
                ) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = Color.Unspecified,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        onClick = {
                            settingsViewModel.skipWelcomePage()
                        }
                    ) {
                        Text("Пропустить")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    colors = ButtonDefaults.textButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        if (state.currentPage == state.pageCount.minus(1)) {
                            settingsViewModel.skipWelcomePage()
                        } else {
                            scope.launch {
                                state.animateScrollToPage(state.settledPage.plus(1))
                            }
                        }

                    }
                ) {
                    Text("Вперёд")
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
