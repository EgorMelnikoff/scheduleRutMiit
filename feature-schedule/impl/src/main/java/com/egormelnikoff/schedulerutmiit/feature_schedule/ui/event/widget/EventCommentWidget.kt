package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventAction
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState

fun LazyListScope.eventCommentWidget(
    eventState: EventState.Loaded,
    onAction: (EventAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier.padding(horizontal = AppTheme.paddings.padding16)
        ) {
            ColumnGroup(
                title = stringResource(R.string.comment),
                titleColor = AppTheme.colors.primary,
                withBackground = false,
                items = listOf {
                    AppTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        value = eventState.comment,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Default
                        ),
                        maxSymbols = 100,
                        placeholderText = stringResource(R.string.enter_comment),
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = eventState.comment.isNotEmpty(),
                                enter = scaleIn(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(500))
                            ) {
                                IconButton(
                                    onClick = {
                                        onAction(EventAction.OnCommentChanged(""))
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.clear),
                                        contentDescription = null,
                                        tint = AppTheme.colors.primary
                                    )
                                }
                            }
                        }
                    ) { comment ->
                        onAction(EventAction.OnCommentChanged(comment))
                    }
                }
            )
        }
    }
}