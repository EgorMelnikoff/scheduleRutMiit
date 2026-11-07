package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review.widget.ReviewScreenWidget
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.reviewStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    contentPadding: PaddingValues,
) {
    val viewModel = hiltViewModel<ReviewViewModel>()
    viewModel.Initialize()
    val reviewScreenState by viewModel.state.collectAsStateWithLifecycle()

    ReviewScreenWidget(
        contentPadding = contentPadding,
        reviewState = reviewScreenState,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Preview(locale = "ru")
@Composable
fun ReviewScreenPreview(){
    PreviewLightTheme {
        ReviewScreenWidget(
            contentPadding = PaddingValues(0.dp),
            reviewState = reviewStateMock,
            onAction = {}
        )
    }
}
