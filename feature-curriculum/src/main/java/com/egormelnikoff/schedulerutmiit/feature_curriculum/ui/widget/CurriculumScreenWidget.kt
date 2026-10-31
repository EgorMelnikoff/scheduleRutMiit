package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.utils.error.getMessage
import com.egormelnikoff.schedulerutmiit.core.ui.components.snackbar_host.AppSnackbarHost
import com.egormelnikoff.schedulerutmiit.core.ui.widget.EmptyScreen
import com.egormelnikoff.schedulerutmiit.core.ui.widget.LoadingScreen
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumAction
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumState

@Composable
internal fun CurriculumScreenWidget(
    state: CurriculumState,
    snackBarHostState: SnackbarHostState,
    onAction: (CurriculumAction) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = {
            AppSnackbarHost(
                snackBarHostState = snackBarHostState
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding() + 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CurriculumSearchInputWidget(
                state = state,
                onAction = onAction
            )

            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = state,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { animatedState ->
                when {
                    animatedState.isLoading -> {
                        LoadingScreen()
                    }

                    animatedState.error != null -> {
                        EmptyScreen(
                            subtitle = animatedState.error.getMessage(LocalContext.current)
                        )
                    }

                    animatedState.isEmptyQuery -> {
                        EmptyScreen(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            subtitle = stringResource(R.string.enter_your_query)
                        )
                    }

                    animatedState.subjectsList.isEmpty() ->
                        EmptyScreen(
                            subtitle = stringResource(R.string.nothing_found)
                        )

                    else -> {
                        CurriculumScreenContentWidget(
                            contentPadding, animatedState
                        )
                    }
                }
            }
        }
    }

}