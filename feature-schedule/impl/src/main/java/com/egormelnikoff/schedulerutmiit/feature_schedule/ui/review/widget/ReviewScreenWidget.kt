package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppAlertBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.reviewStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreenWidget(
    contentPadding: PaddingValues,
    reviewState: ReviewState,
    onAction: (ReviewAction) -> Unit,
) {
    val topBarHeightPx = with(LocalDensity.current) {
        60.dp.toPx()
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val overlappedFraction by remember {
        derivedStateOf {
            scrollBehavior.state.overlappedFraction
        }
    }
    var showTopBarActions by remember {
        mutableStateOf(false)
    }

    if (reviewState.usedPhoto) {
        ReviewScreenStatusBarEffect(
            overlappedFraction = overlappedFraction,
            isDarkTheme = reviewState.theme.isDarkTheme()
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ReviewTopBarWidget(
                scrollBehavior = scrollBehavior,
                overlappedFraction = overlappedFraction,
                showTopBarActions = showTopBarActions,
                reviewState = reviewState,
                onAction = onAction
            )
        }
    ) { internalPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
            contentPadding = PaddingValues(
                top = if (reviewState.usedPhoto) 0.dp else internalPadding.calculateTopPadding() + AppTheme.paddings.padding12,
                bottom = contentPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            if (reviewState.usedPhoto) {
                reviewImageWidget(reviewState)

                reviewActionsWidget(
                    onChangeTopBarVisibility = { y ->
                        showTopBarActions = y <= topBarHeightPx
                    },
                    onAction = onAction
                )
            }
            if (reviewState.namedSchedules.isNotEmpty()) {
                reviewSchedulesWidget(
                    reviewState = reviewState,
                    onAction = onAction
                )
            }
            reviewServicesWidget(
                reviewState = reviewState,
                onAction = onAction
            )
        }
    }

    reviewState.scheduleSheet?.let {
        ReviewScheduleSheet(
            namedSchedule = it,
            onAction = onAction
        )
    }

    reviewState.scheduleDeleteSheet?.let {
        AppAlertBottomSheet(
            dialogTitle = "${stringResource(R.string.delete_schedule)}?",
            dialogText = stringResource(R.string.impossible_restore_eventextra),
            onDismissRequest = {
                onAction(
                    ReviewAction.OnShowDeleteScheduleSheet(null)
                )
            },
            onConfirmation = {
                onAction(
                    ReviewAction.OnDeleteNamedSchedule(
                        id = it.id,
                        isDefault = it.isDefault
                    )
                )
            }
        )
    }
}

@Preview(locale = "ru")
@Composable
fun ReviewScreenWidgetWithImagePreview() {
    PreviewLightTheme {
        ReviewScreenWidget(
            contentPadding = PaddingValues(AppTheme.paddings.none),
            reviewState = reviewStateMock,
            onAction = {}
        )
    }
}

@Preview(locale = "ru")
@Composable
fun ReviewScreenWidgetWithoutImagePreview() {
    PreviewLightTheme {
        ReviewScreenWidget(
            contentPadding = PaddingValues(AppTheme.paddings.none),
            reviewState = reviewStateMock.copy(
                usedPhoto = false
            ),
            onAction = {}
        )
    }
}