package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.widget

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ColumnGroup
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenAction
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event.EventScreenState

fun LazyListScope.eventCommentWidget(
    eventScreenState: EventScreenState.Loaded,
    onAction: (EventScreenAction) -> Unit
) {
    item {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            ColumnGroup(
                title = stringResource(R.string.comment),
                titleColor = MaterialTheme.colorScheme.primary,
                withBackground = false,
                items = listOf {
                    AppTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        value = eventScreenState.comment,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Default
                        ),
                        maxSymbols = 100,
                        placeholderText = stringResource(R.string.enter_comment),
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = eventScreenState.comment.isNotEmpty(),
                                enter = scaleIn(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(500))
                            ) {
                                IconButton(
                                    onClick = {
                                        onAction(EventScreenAction.OnCommentChanged(""))
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.clear),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    ) { comment ->
                        onAction(EventScreenAction.OnCommentChanged(comment))
                    }
                }
            )
        }
    }
}