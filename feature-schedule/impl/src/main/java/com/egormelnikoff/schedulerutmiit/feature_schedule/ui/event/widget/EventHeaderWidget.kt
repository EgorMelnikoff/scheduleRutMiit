package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.utils.extension.customToString
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.eventStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventHeaderWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit,
) {
    val context = LocalContext.current

    AppTopBar(
        navAction = {
            onAction(EventAction.OnBackClicked)
        },
        actions = {
            IconButton(
                onClick = {
                    onAction(
                        EventAction.OnSendClicked(
                            eventState.event.customToString(
                                context
                            )
                        )
                    )
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.share),
                    contentDescription = null,
                    tint = AppTheme.colors.onBackground
                )
            }

            IconButton(
                onClick = {
                    onAction(EventAction.OnShowMoreDialog(true))
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                    contentDescription = null,
                    tint = AppTheme.colors.onBackground
                )
            }

        }
    )
}

@Preview(locale = "ru")
@Composable
fun EventHeaderPreview() {
    PreviewLightTheme {
        EventHeaderWidget(eventStateMock) { }
    }
}