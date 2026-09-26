package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButtonRow
import com.egormelnikoff.schedulerutmiit.core.ui.elements.RoundedBox
import com.egormelnikoff.schedulerutmiit.core.ui.theme.PreviewLightTheme
import com.egormelnikoff.schedulerutmiit.schedule.ui.utils.addEventStateMock

@Composable
fun RecurrenceWidget(
    maxInterval: Int,
    currentInterval: Int,
    currentPeriod: Int,
    onSelectInterval: (Int) -> Unit,
    onSelectPeriod: (Int) -> Unit
) {
    Column {
        RoundedBox(
            backgroundColor = Color.Unspecified,
            title = stringResource(R.string.repetition),
        ) {
            CustomButtonRow(
                selectedElement = currentInterval,
                elements = (1..maxInterval).toList(),
                onClick = { element ->
                    onSelectInterval(element)
                },
                label = { element ->
                    Text(
                        text = if (element.second == 1) {
                            stringResource(R.string.every_week)
                        } else {
                            stringResource(R.string.once_week, element.second)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = currentInterval > 1,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                ColumnGroup(
                    title = stringResource(R.string.week, ""),
                    withBackground = false,
                    items = listOf {
                        CustomButtonRow(
                            selectedElement = currentPeriod,
                            elements = (1..maxInterval).toList(),
                            onClick = { element ->
                                onSelectPeriod(element)
                            },
                            label = { element ->
                                Text(
                                    text = element.second.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(locale = "ru")
@Composable
fun RecurrenceWidgetPreview() {
    PreviewLightTheme {
        RecurrenceWidget(
            maxInterval = addEventStateMock.schedule.recurrence!!.interval,
            currentInterval = addEventStateMock.editForm.interval,
            currentPeriod = addEventStateMock.editForm.period,
            onSelectInterval = {},
            onSelectPeriod = {},
        )
    }
}

