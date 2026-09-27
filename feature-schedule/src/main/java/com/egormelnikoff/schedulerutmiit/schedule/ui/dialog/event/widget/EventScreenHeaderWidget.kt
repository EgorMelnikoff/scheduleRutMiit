package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.top_bar.AppTopBar
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.data.extension.customToString
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.eventStateMock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenHeaderWidget(
    eventScreenState: EventScreenState.Loaded,
    onAction: (EventScreenAction) -> Unit,
) {
    val context = LocalContext.current

    AppTopBar(
        navAction = {
            onAction(EventScreenAction.OnBackClicked)
        },
        actions = {
            IconButton(
                onClick = {
                    onAction(
                        EventScreenAction.OnSendClicked(
                            eventScreenState.event.customToString(
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
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = {
                    onAction(EventScreenAction.OnShowMoreDialog(true))
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.more_vert),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

        }
    )
}

@Preview(locale = "ru")
@Composable
fun EventScreenHeaderPreview() {
    PreviewLightTheme {
        EventScreenHeaderWidget(eventStateMock) { }
    }
}