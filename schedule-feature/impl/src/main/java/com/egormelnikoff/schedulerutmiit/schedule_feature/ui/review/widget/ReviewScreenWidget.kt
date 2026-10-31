package com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.alert_dialog.AppAlertDialog
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewAction
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.review.ReviewState
import com.egormelnikoff.schedulerutmiit.schedule_feature.ui.utils.reviewStateMock

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
        ReviewScreenEffect(
            overlappedFraction, reviewState
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
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = if (reviewState.usedPhoto) 0.dp else internalPadding.calculateTopPadding() + 12.dp,
                bottom = contentPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
        AppAlertDialog(
            dialogIcon = ImageVector.vectorResource(R.drawable.delete),
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
fun ReviewScreenWidgetWithImagePreview(){
    PreviewLightTheme {
        ReviewScreenWidget(
            contentPadding = PaddingValues(0.dp),
            reviewState = reviewStateMock,
            onAction = {}
        )
    }
}

@Preview(locale = "ru")
@Composable
fun ReviewScreenWidgetWithoutImagePreview(){
    PreviewLightTheme {
        ReviewScreenWidget(
            contentPadding = PaddingValues(0.dp),
            reviewState = reviewStateMock.copy(
                usedPhoto = false
            ),
            onAction = {}
        )
    }
}